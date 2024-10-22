import sqlite3
from pymystem3 import Mystem
import os


# Поисковик
class Searcher:

    # 0. Конструктор Инициализация паука с параметрами БД
    def __init__(self, mainUrl):

        self.path = os.path.dirname(os.path.abspath(__file__))
        self.dbFileName = os.path.join(self.path, 'crawl.db')
        self.db = sqlite3.connect(self.dbFileName)

        self.m = Mystem()

        return

    # 0. Деструктор
    def __del__(self):
        return


    # Возвращает id слова wordlist
    def getWordsIds(self): 
        return 

    # Получить url по url_id
    def getUrlName(self): 
        return

    # формирует таблицу со всеми сочетаниями wordLocation слов поиска для всех urlList
    def getMatchRows(self):
        return

    # нормализация значений метрик 
    def normalizeScores(self):
        return

    # вычисление значения метрики 
    def pageRank(self):
        return

    # создать страницу с помеченныит словами
    def createMarkedHtmlFile(self):
        return

    #  получит список вхождений комбинаций слов, вызовет расчет рангов, выводит отсортированный список ранжированных url.
    def getSortedList(self):
        return

    # Непосредственно сам поиск getSortedList
    def search(self, firstWord, secondWord):

        # создание необходимых таблиц в БД
        # выполнение нескольких итераций расчета ранга ????
        # получение рангов, нормализация, сортировку и вывод результата

        # Результат представить в виде Табл.2 (упорядочить строки по убыванию значений M3).
        
        # https://docs.google.com/document/d/1FyixYQqyNpHFWvzoPq2rhAFm3-dsXZvc/edit
        
        return  
    

def main():
    
    searcher = Searcher()
    searcher.search()


if __name__ == '__main__':
    main()