from bs4 import BeautifulSoup
import requests
import sqlite3
import pymorphy3
from pymystem3 import Mystem
import csv
import re

# Заполняем БД
def createTables(dbFileName):
    
    cur = dbFileName.cursor()
    
    cur.execute("CREATE TABLE db_urlList("
                "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                "url TEXT)"
    )

    cur.execute("CREATE TABLE db_wordList("
                "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                "word TEXT)"
    )
    

    cur.execute("CREATE TABLE db_wordLocation("
                "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                "fk_word_id INTEGER, "
                "fk_url_id INTEGER, "
                "location INTEGER)"
    )

    cur.execute("CREATE TABLE db_linkWord("
                "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                "fk_word_id INTEGER, "
                "fk_link_id INTEGER)"
    )

    cur.execute("CREATE TABLE db_linkBetweenURL("
                "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                "fk_fromURL_id INTEGER, "
                "fk_toURL_id INTEGER)"
    )


    dbFileName.commit()

    return

# Удаляем БД
def deleteTables(dbFileName):
    
    cur = dbFileName.cursor()

    cur.execute("DROP TABLE db_urlList")
    cur.execute("DROP TABLE db_wordList")
    cur.execute("DROP TABLE db_wordLocation")
    cur.execute("DROP TABLE db_linkWord")
    cur.execute("DROP TABLE db_linkBetweenURL")

    dbFileName.commit()

    return

# Паук
class Crawler:

    # 0. Конструктор Инициализация паука с параметрами БД
    def __init__(self, dbFileName, mainUrl):
        self.db = sqlite3.connect(dbFileName)
        # createTables(self.db)
        
        self.mainUrl = mainUrl
        self.functors_pos = {'INTJ', 'PRCL', 'CONJ', 'PREP'}
        self.m = Mystem()
        self.morth = pymorphy3.MorphAnalyzer()

        return

    # 0. Деструктор
    def __del__(self):
        # deleteTables(self.db)
        return


    # 1. Индексирование одной страницы
    def addIndex(self, soup, url):
        if (self.isIndexed(url)):  #  если страница уже проиндексирована, то ее не индексируем isIndexed
            return

        print(f"Indexing: {url}")

        textList = self.getTextOnly(soup)
        
        if textList == None:
            return

        url_id = self.getEntryId('db_urlList', 'url', url)  #   Получаем идентификатор URL getEntryId
        
        location = 0
        for word in textList:
            location = location + 1
            word_id = self.getEntryId('db_wordList', 'word', word)
            word_location_id = self.getEntryId('db_wordLocation', 'fk_url_id', url_id, 'fk_word_id', word_id, 'location', location) 

        return


    # 2. Получение текста страницы
    def getTextOnly(self, soup):
        
        textList = []
        if (soup.find('div', class_='post__content') == None):
            return
        
        pMain = soup.find('div', class_='post__content').find_all(['p', 'blockquote'])

        for data in pMain:
            if data.text == '':
                continue
            textList.append(data.text)

        for i in range(len(textList)):
            textList[i] = self.textFilter(textList[i]) 
       
        newTextList = self.doubleArrayToSingle(textList)

        return newTextList

    # Удалить все кроме букв и цифр
    def clearText(self, text):
        return re.sub(r'[\W ]+', ' ', text)

    # 3. Разбиение текста на слова
    def separateWords(self, text):
        newClearText = self.clearText(text)
        return newClearText.split()

    # 4. Проиндексирован ли URL (проверка наличия URL в БД)
    def isIndexed(self, url):
        
        cur = self.db.cursor()
        
        request = cur.execute(f"SELECT id FROM db_urlList WHERE url = '{url}' ").fetchone()

        if(request != None): 
            request = cur.execute(f"SELECT fk_word_id FROM db_wordLocation WHERE fk_url_id = '{request}' ").fetchone()
            
            if(request != None):
                return True
            
        return False
 

    # 5. Добавление ссылки с одной страницы на другую
    def addLinkRef(self, urlFrom:str, urlTo:str, linkText:str):
        
        cur = self.db.cursor()

        urlFrom_id = self.getEntryId('db_urlList', 'url', urlFrom)
        urlTo_id = self.getEntryId('db_urlList', 'url', urlTo)

        link_id = self.getEntryId('db_linkBetweenURL', 'fk_fromURL_id', urlFrom_id, 'fk_toURL_id', urlTo_id)        

        filteredTextLink = self.textFilter(linkText)

        for word in filteredTextLink:
            word_id = self.getEntryId('db_wordList', 'word', word)
            link_word_id = self.getEntryId('db_linkWord', 'fk_link_id', link_id, 'fk_word_id', word_id)

        self.db.commit()


    # 6. Непосредственно сам метод сбора данных.
    # Начиная с заданного списка страниц, выполняет поиск в ширину
    # до заданной глубины, индексируя все встречающиеся по пути страницы
    def crawl(self, urlList, maxDepth=1):

        for currDepth in range(0, maxDepth):
        #     # Вар.1. обход каждого url на текущей глубине
            newUrlList = []
            for url in urlList:
                
                html_doc = requests.get(url).text   # получить HTML-код страницы по текущему url    
                soup = BeautifulSoup(html_doc, "html.parser")   # использовать парсер для работа тегов
            
                filteredLinks, textLinks = self.linkFilter(soup)
                
                for i in range(len(filteredLinks)):
                    print(f"{url} -> {filteredLinks[i]}")
                    self.addLinkRef(url, filteredLinks[i], textLinks[i])

                self.addIndex(soup, url) # вызвать функцию класса Crawler для добавления содержимого в индекс

                self.check_db()

                newUrlList.append (filteredLinks)

            urlList = self.doubleArrayToSingle(newUrlList)

        return  


    # 7. Вспомогательная функция для получения идентификатора и
    # добавления записи, если такой еще нет
    def getEntryId(self, tableName, fieldNameFirst, valueFirst, fieldNameSecond = False, valueSecond = False, fieldNameThird = False, valueThird = False):
        cur = self.db.cursor()

        fieldNameList = [fieldNameSecond, fieldNameThird]
        valueList = [valueSecond, valueThird]

        requestGet = f"SELECT id FROM '{tableName}' WHERE {fieldNameFirst} = '{valueFirst}'"
        
        for i in range(len(valueList)):
            if valueList[i]:
                requestGet+= f" AND {fieldNameList[i]} = '{valueList[i]}'"
        
        request_id = cur.execute(f"{requestGet}").fetchone()

        if request_id != None:
            return request_id[0]

        requestInsert = f"INSERT INTO '{tableName}' ({fieldNameFirst}"

        for i in range(len(valueList)):
            if valueList[i]:
                requestInsert += f", {fieldNameList[i]}"
        requestInsert += f") VALUES ('{valueFirst}'"

        for i in range(len(valueList)):
            if valueList[i]:
                requestInsert += f", '{valueList[i]}'"
        requestInsert += f")"

        cur.execute(f"{requestInsert}")
        self.db.commit()

        request_id = cur.execute(f"{requestGet}").fetchone()

        return request_id[0]


    # Форматируем ссылку - Очищаем ссылку от ненужный хуков и тд
    def linkFilter(self, soup):
            try:
                links = soup.find('main').find_all('a', href=True)
            except:
                return [], []

            filteredLinks = []
            textLinks = []
            newfilteredLinks = []
            newtextLinks = []

            for data in links:
                filteredLinks.append(data.get("href"))
            
            for data in links:
                textLinks.append(data.text)

            for i in range(len(filteredLinks)):
                if textLinks[i] == '\n\n':
                    continue
                if textLinks[i] == '':
                    continue
                if '.doc' in filteredLinks[i]:
                    continue
                if 'https' not in filteredLinks[i]:
                    filteredLinks[i] = self.mainUrl + filteredLinks[i]
                if '?' in filteredLinks[i]:
                    filteredLinks[i] = filteredLinks[i].split('?', 1)[0]

                newfilteredLinks.append(filteredLinks[i])
                newtextLinks.append(textLinks[i])

            return newfilteredLinks, newtextLinks


    # Двумерный массив в одномерный
    def doubleArrayToSingle(self, doubleArray):
        singleArray = []
        for arr in doubleArray:
            for element in arr:
                singleArray.append(element)
            
        return singleArray


    # Удалить все союзы междометия и тд
    def pos(self, word):
        return self.morth.parse(word)[0].inflect({'gent'}).word


    # Форматируем текст - преобразуем в именительный паддеж 1 число, удаляем все союзы и тд
    def textFilter(self, text):
        newText = []
        textList = []

        clearText = self.clearText(text)
        lemmas = self.m.lemmatize(clearText)

        for lem in lemmas:
            if ' ' not in lem:
                textList.append(lem)

        for word in textList:
            try:
                if self.pos(word) not in self.functors_pos:
                    newText.append(word)
            except:
                continue

        return newText

    def writeCsv(self, worlListLen, linkBetweenURLLen, UrlListLen, filename = "db_Inserting.csv"):
        with open(filename, 'a', newline='') as csvfile:
            spamwriter = csv.writer(csvfile, delimiter=' ', quotechar='|', quoting=csv.QUOTE_MINIMAL)
            spamwriter.writerow([worlListLen, linkBetweenURLLen, UrlListLen])

    def check_db(self):
        cur = self.db.cursor()

        requestGetWorlList = f"SELECT * FROM db_wordList"
        requestGetLinkBetweenURL = f"SELECT * FROM db_linkBetweenURL"
        requestGetUrlList = f"SELECT * FROM db_urlList"

        worlListLen = len(cur.execute(f"{requestGetWorlList}").fetchall())
        linkBetweenURLLen = len(cur.execute(f"{requestGetLinkBetweenURL}").fetchall())
        UrlListLen = len(cur.execute(f"{requestGetUrlList}").fetchall())

        self.writeCsv(worlListLen, linkBetweenURLLen, UrlListLen)

        return

    # def getManyURL(self):
        # cur = self.db.cursor()

    #     requestGet = f"SELECT url FROM db_urlList"
    #     urls = cur.execute(f"{requestGet}").fetchall()
    #     self.db.commit()

    #     urlList = []

    #     for i in urls:
    #         urlList.append(i[0][8:].split('/', 1)[0])

    #     sortedURLs = sorted(urlList)

    #     result = []
    #     count = []

    #     for i in range(500):
    #         count.append(1)

    #     j=0

    #     for i in range(len(sortedURLs)):
    #         sortedURLs[i] = sortedURLs[i].split('http:', 1)[0]

    #     for i in range(len(sortedURLs)-1):
            
    #         if sortedURLs[i] == sortedURLs[i+1]:
    #             count[j-1] += 1
                
    #         if sortedURLs[i] != sortedURLs[i+1]:
    #             result.append(sortedURLs[i+1])
    #             j+=1


    #     printedList = []
    #     for i in range(len(result)):
    #         printedList.append(f"{count[i]} = {result[i]}")

    #     sortedPrintedList = sorted(printedList, reverse=True)
        
    #     for i in range(20):
    #         print(sortedPrintedList[i])
        # return

        

def main():

    url = 'https://www.journal.zarplata.ru'
    urlList = ['https://www.journal.zarplata.ru']
    for i in range(1, 90):   #90
        url_page = url + '/page/' + str(i)
        urlList.append(url_page)
    
    crawler = Crawler('crawl.db', url)
    crawler.crawl(urlList, maxDepth=2)



if __name__ == '__main__':
    main()