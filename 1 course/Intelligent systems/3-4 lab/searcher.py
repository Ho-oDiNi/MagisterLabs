from bs4 import BeautifulSoup
import requests
import sqlite3
from pymystem3 import Mystem
import os
import re

# Заполнить новую таблицу рейтинга страниц
def fill_db_pageRank(db):

    cur = db.cursor()
    requestGet = f"SELECT id FROM db_urlList"
    urlList = cur.execute(requestGet).fetchall()

    for url in urlList:
        requestSet = f"INSERT INTO db_pageRank (url_id) VALUES ({url[0]})"
        cur.execute(requestSet)

    db.commit()

    return

# Создать новую таблицу рейтинга страниц
def create_db_pageRank(db):

    cur = db.cursor()
    cur.execute('DROP TABLE IF EXISTS db_pageRank')
    cur.execute("""CREATE TABLE IF NOT EXISTS db_pageRank(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        url_id INTEGER,
                        indexed DEFAULT 0,
                        score REAL DEFAULT 1.0
                    );""")
    db.commit()

    fill_db_pageRank(db)

    return


# Поисковик
class Searcher:

    # 0. Конструктор
    def __init__(self):

        self.path = os.path.dirname(os.path.abspath(__file__))
        self.dbFileName = os.path.join(self.path, 'crawl.db')
        self.db = sqlite3.connect(self.dbFileName)

        create_db_pageRank(self.db)

        self.m = Mystem()

        return

    # 0. Деструктор
    def __del__(self):
        return

    ############################################################
    #########              Все о БД                    ######### 
    ############################################################

    # Получить id слова по word в табл. wordlist
    def getWordId(self, word): 
        cur = self.db.cursor()

        requestGet = f"SELECT id FROM db_wordList WHERE word = '{word}'"
        word_id = cur.execute(f"{requestGet}").fetchone()

        if (word_id == None):
            raise Exception('The first word were not found')
            
        return word_id[0]



    # Получить url по url_id в табл. db_urlList
    def getUrlName(self, url_id): 
        cur = self.db.cursor()

        requestGet = f"SELECT url FROM db_urlList WHERE id = '{url_id}'"
            
        url = cur.execute(f"{requestGet}").fetchone()

        if (url == None):
            raise Exception('The URL were not found')

        return url[0]



    # Получить score по url_id в таблице db_pageRank
    def getPageRank(self, url_id):
        cur = self.db.cursor()

        indexed = cur.execute(f"SELECT indexed FROM db_pageRank WHERE url_id = {url_id}").fetchone()[0]
        if(indexed==0):
            score = 0
        else:
            score = cur.execute(f"SELECT score FROM db_pageRank WHERE url_id = {url_id}").fetchone()[0]

        return score



    ############################################################
    #########        Все о искомых Словах              ######### 
    ############################################################

    # Считать слова
    def inputWords(self):
        print("Enter Words")
        words = input()
        return words

    # Преобразовать считанные слова в 1 род им. паддеж 
    def filterWords(self, words):

        wordList = []
        lemmas = self.m.lemmatize(words)

        for lem in lemmas:
            if ' ' not in lem:
                wordList.append(lem)

        print(wordList)

        if len(wordList) != 3:
            raise Exception('Please inter a another words')

        return wordList[0], wordList[1]
    


    # формирует таблицу со всеми сочетаниями искомых слов во всех url
    def getMatchRows(self, fstWord, sndWord):
        cur = self.db.cursor()

        firstWord_id = self.getWordId(fstWord)
        sndWord_id = self.getWordId(sndWord)

        requestGet =  f"SELECT "
        requestGet += f"T1.fk_url_id, T1.location, T2.location "
        requestGet += f"FROM db_wordLocation AS T1 "
        requestGet += f"INNER JOIN db_wordLocation AS T2 ON T1.fk_url_id = T2.fk_url_id " 
        requestGet += f"WHERE T1.fk_word_id = '{firstWord_id}' AND T2.fk_word_id = '{sndWord_id}'"

        DBList = cur.execute(f"{requestGet}").fetchall()
        DBList.append((0, 0, 0))

        self.printTable(fstWord, sndWord, DBList)

        if (DBList == None):
            raise Exception('The first URL list were not found')

        newDBList = self.DBListToTrible(DBList)

        return newDBList


    # Форматируем текст - преобразуем в именительный паддеж 1 число, удаляем все союзы и тд
    def textFilter(self, text):
        textList = []

        clearText = self.clearText(text)
        lemmas = self.m.lemmatize(clearText)

        for lem in lemmas:
             if (' ' not in lem) and ('\n' not in lem):
                textList.append(lem)


        return textList, clearText


    ############################################################
    #########            Все о метриках                ######### 
    ############################################################

    
    # вычисление значения метрики Частоты слов на странице
    def freqMetrika(self, DBList, fstWord, sndWord):
        freqMetr = []

        cur = self.db.cursor()
        firstWord_id = self.getWordId(fstWord)
        sndWord_id = self.getWordId(sndWord)

        for page in DBList:
            url = page[0][0]

            requestFstGet = f"SELECT COUNT(id) FROM db_wordLocation WHERE fk_url_id = {url} AND fk_word_id = {firstWord_id}"
            requestSndGet = f"SELECT COUNT(id) FROM db_wordLocation WHERE fk_url_id = {url} AND fk_word_id = {sndWord_id}"

            fstCount = cur.execute(f"{requestFstGet}").fetchone()[0]
            sndCount = cur.execute(f"{requestSndGet}").fetchone()[0]

            freqMetr.append((url, sndCount, fstCount))

        return freqMetr


    # вычисление значения метрики отдаленности слов между друг другом на странице
    def relPosMetrika(self, DBList):
        relPosMetr = []

        for page in DBList:
            url = page[0][0]

            minPos = abs(page[0][2] - page[0][1])
            for row in page:
                relPos = abs(row[2] - row[1])

                if relPos < minPos:
                    minPos = relPos

            relPosMetr.append((url, minPos))

        return relPosMetr


    # вычисление значения метрики отдаленности слов от начала страницы
    def absPosMetrika(self, DBList):
        absPosMetr = []
        
        for page in DBList:
            url = page[0][0]

            fstWordMin = page[0][2]
            sndWordMin = page[0][1]

            absPosMetr.append((url, fstWordMin, sndWordMin))

        return absPosMetr


    ############################################################
    #########         Все о нормализации              ######### 
    ############################################################


    # Получить максимально возможное отдаление слова
    def getMaxLocation(self, url_id):
        cur = self.db.cursor()
        requestGet = f"SELECT MAX(location) FROM db_wordLocation WHERE fk_url_id = {url_id}"
        maxLocation = cur.execute(f"{requestGet}").fetchone()[0]

        return maxLocation + 1



    # Нормализация значений метрик 
    def normalizeScores(self, absPosMetrika, relPosMetrika, freqMetrika):

        # Для absPosMetrika()
        pagesM1 = []
        for page in absPosMetrika:
            url_id = page[0]
            maxLocation = self.getMaxLocation(url_id)
            rangeLocation = maxLocation - 1
            
            fM1 = (maxLocation - page[1])/rangeLocation
            sM1 = (maxLocation - page[2])/rangeLocation

            M1 = (fM1 + sM1) / 2

            pagesM1.append((url_id, M1))

        # Для relPosMetrika()
        pagesM2 = []
        for page in relPosMetrika:
            url_id = page[0]
            maxLocation = self.getMaxLocation(url_id)
            rangeLocation = maxLocation - 1

            M2 = (maxLocation - page[1]) / rangeLocation

            pagesM2.append((url_id, M2))

        # Для freqMetrika()
        pagesM3 = []
        for page in freqMetrika:
            url_id = page[0]
            maxLocation = self.getMaxLocation(url_id)
            rangeLocation = maxLocation - 1

            fM3 = (page[1] - 1) / rangeLocation
            sM3 = (page[2] - 1) / rangeLocation

            M3 = (fM3 + sM3) / 2

            pagesM3.append((url_id, M3))


        # Суммирование значений M1, M2, M3 и усреднение с учетом PageRank (ранга страницы)
        pageMetriks = []
        for i in range(len(pagesM1)):
            url_id = pagesM1[i][0]
            url = self.getUrlName(url_id)

            M1 = pagesM1[i][1]
            M2 = pagesM2[i][1]
            M3 = pagesM3[i][1]

            pageRank = self.getPageRank(url_id)

            M4 = pageRank*(M1 + M2 + M3)/3

            pageMetriks.append((url_id, round(M1, 2), round(M2, 2), round(M3, 2), round(M4, 4), url))

        return pageMetriks



    # Нормализация значений ранга страниц
    def normalizeRank(self):
        cur = self.db.cursor()

        maxScore = cur.execute(f"SELECT MAX(score) FROM db_pageRank").fetchone()[0]
        urlList = cur.execute(f"SELECT id FROM db_pageRank").fetchall()

        for url in urlList:
            mainUrl = url[0]
            score = cur.execute(f"SELECT score FROM db_pageRank WHERE id = {mainUrl}").fetchone()[0]

            normolizeScore = round((score/maxScore), 2)
            if(normolizeScore==0):
                normolizeScore = 0.01
            cur.execute(f"UPDATE db_pageRank SET score = {normolizeScore} WHERE url_id = {mainUrl}")
            self.db.commit()
        
        return




    ############################################################
    #########         Все о ранге страницы             ######### 
    ############################################################


    # Нахождение ранга страниц
    def pageRank(self, iterations=5, d = 0.85):

        cur = self.db.cursor()

        requestGet = f"SELECT url_id FROM db_pageRank"
        urlList = cur.execute(requestGet).fetchall()

        for i in range(iterations):
            print("Итерация %d" % (i+1))
            

            for url in urlList:
                mainUrl = url[0]
    
                urlFromRefToMainList = cur.execute(f"SELECT fk_fromURL_id FROM db_linkBetweenURL WHERE fk_toURL_id = {mainUrl}").fetchall()

                urlP = 1 - d

                if (urlFromRefToMainList):
                    for urlTo in urlFromRefToMainList:
                        score = self.getPageRank(urlTo[0])
                        if(score == 0):
                            PRef = cur.execute(f"SELECT score FROM db_pageRank WHERE url_id = {urlTo[0]}").fetchone()[0]
                            CRef = cur.execute(f"SELECT COUNT(id) FROM db_linkBetweenURL WHERE fk_fromURL_id = {urlTo[0]}").fetchone()[0]                            
                            urlP += d * (PRef/CRef)
                            cur.execute(f"UPDATE db_pageRank SET score = {round(urlP, 2)} WHERE url_id = {urlTo[0]}")
                            cur.execute(f"UPDATE db_pageRank SET indexed = 1 WHERE url_id = {urlTo[0]}")
                            self.db.commit()
                        else:
                            urlP += score

                cur.execute(f"UPDATE db_pageRank SET score = {round(urlP, 2)} WHERE url_id = {mainUrl}")
                cur.execute(f"UPDATE db_pageRank SET indexed = 1 WHERE url_id = {mainUrl}")
                self.db.commit()

            self.normalizeRank()
       
        return




    ############################################################
    #########     Все о создании помеченной HTML       ######### 
    ############################################################


    # Нахождение пар (отфильтрованное слово - слово на странице)
    def createMatch(self, fstWord, sndWord, pMain):

        textList = []
        filterTextList = []
        clearTextList = []

        for data in pMain:
            if data.text == '':
                continue
            textList.append(data.text)

        for i in range(len(textList)):
            filteredText, clearText = self.textFilter(textList[i])

            filterTextList.append(filteredText)
            clearTextList.append(clearText)


        matchList = []
        for i in range(len(filterTextList)):
            for j in range(len(filterTextList[i])):
                if (filterTextList[i][j] == fstWord) or (filterTextList[i][j] == sndWord):
                    matchList.append(clearTextList[i].split()[j])

        return matchList



    # Создать страницу с помеченныит словами
    def createMarkedHtmlFile(self, sortedMetrics, fstWord, sndWord):

        urlList = []
        for i in range(3):
            urlList.append(sortedMetrics[i][5])


        for i in range(len(urlList)):
            print(f"Creating HTML file N{i+1}")
            html_doc = requests.get(urlList[i]).text    # получить HTML-код страницы по текущему url    
    
            soup = BeautifulSoup(html_doc, "html.parser")   # использовать парсер для работа тегов
            pMain = soup.find('div', class_='post__content').find_all(['p', 'blockquote'])

            matchList = self.createMatch(fstWord, sndWord, pMain)
            
            newHtml = f""
            for word in html_doc.split():
                if word in matchList:
                    word = f"<span style='background-color: #FFFF00'>{word}</span>"
                newHtml += f" {word} "

            with open(f"page{i}.html", 'w', encoding="utf-8") as f:
                f.write(newHtml)    

        return





    ############################################################
    #########                Доп. Функции              ######### 
    ############################################################


    # Вспомогательное преобразование в тройной массив
    def DBListToTrible(self, DBList):

        count = 0
        for i in range(len(DBList) - 1):
            if DBList[i][0] != DBList[i+1][0]:
                count += 1

        newArray = [list() for _ in range(count)]

        j = 0
        for i in range(len(DBList) - 1):
            newArray[j].append(DBList[i])
            if DBList[i][0] != DBList[i+1][0]:
                j += 1

        return newArray


    # Вывод таблицы 1 в консоль
    def printTable(self, fstWord, sndWord, DBList):
        print("----------------------------------------")
        print(f"url_ID\t|\t{fstWord}\t|\t{sndWord}")
        
        counter = 0
        for row in DBList:
            print(f"{row[0]}\t|\t{row[1]}\t|\t{row[2]}")
            
            counter += 1
            if counter%5==0:
                print("Stop?\nEnter y/n")
                key = input()
                if key.lower()=='y':
                    break

        print("----------------------------------------")
        return


    # Удалить все кроме букв и цифр
    def clearText(self, text):
        return re.sub(r'[\W ]+', ' ', text)





    ############################################################
    #########            Основные Функции              ######### 
    ############################################################

    # Непосредственно сам поиск
    def search(self):

        self.pageRank()
        words = self.inputWords()
        fstWord, sndWord = self.filterWords(words)
        DBList = self.getMatchRows(fstWord, sndWord)
        sortedMetrics = self.getSortedList(DBList, fstWord, sndWord)
        self.createMarkedHtmlFile(sortedMetrics, fstWord, sndWord)

        return  


    # Функция обертка для получения нахождения ранжированных url страниц
    def getSortedList(self, DBList, fstWord, sndWord):
        absPosMetrika = self.absPosMetrika(DBList)
        relPosMetrika = self.relPosMetrika(DBList)
        freqMetrika = self.freqMetrika(DBList, fstWord, sndWord)
        
        pagesMetrics = self.normalizeScores(absPosMetrika, relPosMetrika, freqMetrika)

        sortedMetrics = sorted(pagesMetrics, key=lambda metric: metric[4], reverse=True)

        print(f"\npagesMetrics:\n")
        for row in sortedMetrics:
            print(row)

        return sortedMetrics




# https://docs.google.com/document/d/1FyixYQqyNpHFWvzoPq2rhAFm3-dsXZvc/edit
def main():
    
    searcher = Searcher()
    searcher.search()


if __name__ == '__main__':
    main()