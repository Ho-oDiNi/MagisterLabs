from bs4 import BeautifulSoup
import requests
import sqlite3
import pymorphy3
from pymystem3 import Mystem
import csv
import re
import os

# Заполняем БД
def createTables(dbFileName):
    
    cur = dbFileName.cursor()
    
    cur.execute("CREATE TABLE db_urlList("
                "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                "url TEXT, "
                "isIndexed INTEGER DEFAULT 0)"
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

def createCsv(CSVfilename):
    with open(CSVfilename, 'a', newline='') as csvfile:
        spamwriter = csv.writer(csvfile, delimiter=' ', quotechar='|', quoting=csv.QUOTE_MINIMAL)
        spamwriter.writerow(["UrlListLen", "linkBetweenURLLen", "fltWorlListLen"])

# Паук
class Crawler:

    # 0. Конструктор Инициализация паука с параметрами БД
    def __init__(self, mainUrl):

        self.path = os.path.dirname(os.path.abspath(__file__))

        self.dbFileName = os.path.join(self.path, 'crawl.db')
        self.CSVfilename = os.path.join(self.path, 'db_Inserting.csv')

        self.db = sqlite3.connect(self.dbFileName)

        # createCsv(self.CSVfilename)
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


    # Непосредственно сам метод сбора данных.
    def crawl(self, urlList, maxDepth=1):

        # Обход каждого url на текущей глубине
        for currDepth in range(0, maxDepth):
            
            newUrlList = []
            for url in urlList:

                if(self.isIndexed(url)):
                    continue

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


    ############################################################
    #########            Все о индексации              ######### 
    ############################################################

    # Индексирование одной страницы
    def addIndex(self, soup, url):
        if (self.isIndexed(url)):  #  если страница уже проиндексирована, то ее не индексируем isIndexed
            return

        print(f"Indexing: {url}")

        fltTextList = self.getTextOnly(soup)
        
        url_id = self.getEntryId('db_urlList', 'url', url)  #   Получаем идентификатор URL
        self.addTextToDB(url_id, fltTextList)
        self.urlUpdateIndexed(url_id)

        return


    # Проиндексирован ли URL (проверка наличия URL в БД)
    def isIndexed(self, url):
        
        cur = self.db.cursor()
        request = cur.execute(f"SELECT isIndexed FROM db_urlList WHERE url = '{url}' ").fetchone()
        if(request): 
            if(request[0]):
                return True
            
        return False
 
    # Страница была проиндексирована
    def urlUpdateIndexed(self, urlId):
        cur = self.db.cursor()

        requestUpdate = f"UPDATE db_urlList SET isIndexed = 1 WHERE id = '{urlId}'"
        cur.execute(requestUpdate)

        self.db.commit()




    ############################################################
    #########              Все о ссылках               ######### 
    ############################################################



    # Добавление ссылки с одной страницы на другую
    def addLinkRef(self, urlFrom:str, urlTo:str, linkText:str):
        
        cur = self.db.cursor()

        urlFrom_id = self.getEntryId('db_urlList', 'url', urlFrom)
        urlTo_id = self.getEntryId('db_urlList', 'url', urlTo)

        link_id = self.getEntryId('db_linkBetweenURL', 'fk_fromURL_id', urlFrom_id, 'fk_toURL_id', urlTo_id)        

        textLink = self.textFilter(linkText)

        for word in textLink:
            word_id = self.getEntryId('db_wordList', 'word', word)
            self.setEntry('db_linkWord', 'fk_link_id', link_id, 'fk_word_id', word_id)

        self.db.commit()


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



    ############################################################
    #########              Все о тексте                ######### 
    ############################################################


    # Получение текста страницы
    def getTextOnly(self, soup):
        
        textList = []

        if (soup.find('div', class_='post__content') == None):
            return None, None
        
        pMain = soup.find('div', class_='post__content').find_all(['p', 'blockquote'])

        for data in pMain:
            if data.text == '':
                continue
            textList.append(data.text)

        for i in range(len(textList)):
            textList[i] = self.textFilter(textList[i]) 
       
        filteredTextList = self.doubleArrayToSingle(textList)

        return filteredTextList
        


    # Удалить все кроме букв и цифр
    def clearText(self, text):
        return re.sub(r'[\W ]+', ' ', text)

    # Разбиение текста на слова
    def separateWords(self, text):
        newClearText = self.clearText(text)
        return newClearText.split()

    # Добавление слов в БД
    def addTextToDB(self, url_id, fltTextList):
        
        if fltTextList != None:
            location = 0
            for word in fltTextList:
                location = location + 1
                word_id = self.getEntryId('db_wordList', 'word', word)
                self.setEntry('db_wordLocation', 'fk_url_id', url_id, 'fk_word_id', word_id, 'location', location) 

        return

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


    ############################################################
    #########              Доп. Функции                ######### 
    ############################################################

    # Вспомогательная функция для получения идентификатора и
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


    # Вспомогательная функция для добавления записи
    def setEntry(self, tableName, fieldNameFirst, valueFirst, fieldNameSecond = False, valueSecond = False, fieldNameThird = False, valueThird = False):
        cur = self.db.cursor()

        fieldNameList = [fieldNameSecond, fieldNameThird]
        valueList = [valueSecond, valueThird]

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

        return 


    # Двумерный массив в одномерный
    def doubleArrayToSingle(self, doubleArray):
        singleArray = []
        for arr in doubleArray:
            for element in arr:
                singleArray.append(element)
            
        return singleArray


    # Записать разультаты работы в CSV
    def writeCsv(self, UrlListLen, linkBetweenURLLen, fltWorlListLen):
        with open(self.CSVfilename, 'a', newline='') as csvfile:
            spamwriter = csv.writer(csvfile, delimiter=' ', quotechar='|', quoting=csv.QUOTE_MINIMAL)
            spamwriter.writerow([UrlListLen, linkBetweenURLLen, fltWorlListLen])


    # Проверить количество записей в БД
    def check_db(self):
        cur = self.db.cursor()
       
        requestGetLinkBetweenURL = f"SELECT * FROM db_linkBetweenURL"
        requestGetUrlList = f"SELECT * FROM db_urlList"
        requestGetFLTWorlList = f"SELECT * FROM db_wordList"


        UrlListLen = len(cur.execute(f"{requestGetUrlList}").fetchall())
        linkBetweenURLLen = len(cur.execute(f"{requestGetLinkBetweenURL}").fetchall())
        worlListLen = len(cur.execute(f"{requestGetFLTWorlList}").fetchall())

        
        self.writeCsv(UrlListLen, linkBetweenURLLen, worlListLen)

        return
        


def main():

    url = 'https://www.journal.zarplata.ru'
    urlList = ['https://www.journal.zarplata.ru']
    for i in range(1, 90):   #90
        url_page = url + '/page/' + str(i)
        urlList.append(url_page)
    
    crawler = Crawler(url)
    crawler.crawl(urlList, maxDepth=2)



if __name__ == '__main__':
    main()