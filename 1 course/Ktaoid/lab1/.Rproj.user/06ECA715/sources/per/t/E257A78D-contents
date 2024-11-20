#загрузка данных
data <- read.table(file = "datafile.csv", header = TRUE, sep = ";", fileEncoding = "Windows-1251",
                   na.strings = c("NA", "", " "))
# Просмотр первых строк таблицы
head(data)
# Просмотр таблицы
View(data)
# Имена столбцов
names(data)
# просмотр данных таблицы возраст
data$возраст
# подвыборка возраста
subset(data, data$возраст > 50)
# меняем с int на char
data$X.п.п <- as.character(data$X.п.п)
data$группа <- as.character(data$группа)
data$пол <- as.character(data$пол)
data$профессиональная.специализация..насколько.тесно.профессия.клиента.связана.с.Интернет. <- as.character(data$профессиональная.специализация..насколько.тесно.профессия.клиента.связана.с.Интернет.)
# структура данных
str(data)
# Просмотр статистики по данным
summary(data)
# Минимальное значение возраста
min(data$возраст, na.rm = TRUE)
# Максимальное значение возраста
max(data$возраст, na.rm = TRUE)
# Среднее значение возраста
mean(data$возраст, na.rm = TRUE)
# Стандартное отклонение возраста
sd(data$возраст, na.rm = TRUE)
#Медиана
median(data$возраст, na.rm = TRUE)
# Квартильные значения (первая и третья квартили)
quantile(data$возраст, probs = c(0.25, 0.75), na.rm = TRUE)
# Мода
mode_age <- as.numeric(names(sort(table(data$возраст), decreasing = TRUE)[1]))
print(mode_age)
# Асимметрия packages("e1071")
library(e1071)
skewness(data$возраст, na.rm = TRUE)
# Эксцесс packages("e1071")
kurtosis(data$возраст, na.rm = TRUE)

# Диаграмма рассеяния возраста и дохода
plot(data$возраст, data$средний.доход.в.месяц, 
     xlab = "Возраст", ylab = "Средний доход в месяц", 
     main = "Диаграмма рассеяния: Возраст vs Средний доход")

# Радиальная диаграмма для качественного признака
survey_table <- table(data$степень.активности..участие.в.Интернет.опросах....качественная.оценка, useNA = "no")
print(survey_table)

# Вычисление значений и меток для круговой диаграммы
survey_values <- as.vector(survey_table)
survey_labels <- names(survey_table)
percent_labels <- paste(survey_labels, "(", round(100 * survey_values / sum(survey_values), 1), "%)", sep = "")

# Круговая диаграмма
pie(survey_values, 
    labels = percent_labels, 
    main = "Круговая диаграмма по степени активности в опросах",
    col = rainbow(length(survey_values)))

# Категориальная радиальная диаграмма
par(mfrow = c(2, 3))

# 1. Степень активности по полу
activity_by_gender <- table(data$степень.активности..участие.в.Интернет.опросах....качественная.оценка, data$пол)

# Для мужчин (пол = 1)
activity_male <- activity_by_gender[, "1"]
labels_male <- names(activity_male)
percent_labels_male <- paste(labels_male, "(", round(100 * activity_male / sum(activity_male), 1), "%)", sep = "")
pie(activity_male, labels = percent_labels_male, main = "Степень активности - Мужчины", col = rainbow(length(activity_male)), border = "white")

# Для женщин (пол = 2)
activity_female <- activity_by_gender[, "2"]
labels_female <- names(activity_female)
percent_labels_female <- paste(labels_female, "(", round(100 * activity_female / sum(activity_female), 1), "%)", sep = "")
pie(activity_female, labels = percent_labels_female, main = "Степень активности - Женщины", col = rainbow(length(activity_female)), border = "white")

# 2. Группа по полу
group_by_gender <- table(data$группа, data$пол)

# Для группы 1 (мужчины и женщины)
group_male <- group_by_gender[,"1"]
group_female <- group_by_gender[,"2"]

# Круговая диаграмма для мужчин по группе
percent_labels_group_male <- paste(names(group_male), "(", round(100 * group_male / sum(group_male), 1), "%)", sep = "")
pie(group_male, labels = percent_labels_group_male, main = "Группа - Мужчины", col = c("lightblue", "lightgreen"), border = "white")

# Круговая диаграмма для женщин по группе
percent_labels_group_female <- paste(names(group_female), "(", round(100 * group_female / sum(group_female), 1), "%)", sep = "")
pie(group_female, labels = percent_labels_group_female, main = "Группа - Женщины", col = c("lightblue", "lightgreen"), border = "white")

# 3. Профессиональная специализация по полу
specialization_by_gender <- table(data$профессиональная.специализация..насколько.тесно.профессия.клиента.связана.с.Интернет., data$пол)

# Для мужчин (пол = 1)
specialization_male <- specialization_by_gender[, "1"]
labels_male_specialization <- names(specialization_male)
percent_labels_specialization_male <- paste(labels_male_specialization, "(", round(100 * specialization_male / sum(specialization_male), 1), "%)", sep = "")
pie(specialization_male, labels = percent_labels_specialization_male, main = "Специализация - Мужчины", col = rainbow(length(specialization_male)), border = "white")

# Для женщин (пол = 2)
specialization_female <- specialization_by_gender[, "2"]
labels_female_specialization <- names(specialization_female)
percent_labels_specialization_female <- paste(labels_female_specialization, "(", round(100 * specialization_female / sum(specialization_female), 1), "%)", sep = "")
pie(specialization_female, labels = percent_labels_specialization_female, main = "Специализация - Женщины", col = rainbow(length(specialization_female)), border = "white")

par(mfrow = c(1, 1))


# Категориальная столбиковая диаграмма для количественного признака в зависимости от групповой переменной
# Настройка для отображения 2 строк и 2 столбцов графиков
par(mfrow = c(2, 2))  
# 1. Средний доход по полу (мужчины, женщины)
average_income_by_gender <- tapply(data$средний.доход.в.месяц, data$пол, mean, na.rm = TRUE)
barplot(average_income_by_gender, 
        beside = TRUE, 
        col = c("lightblue", "lightpink"), 
        names.arg = c("Мужчины", "Женщины"),
        main = "Средний доход по полу", 
        ylab = "Средний доход в месяц", 
        ylim = c(0, max(average_income_by_gender)),  # Для более четкого отображения
        border = "white")
# 2. Средний доход по группе (1, 2)
average_income_by_group <- tapply(data$средний.доход.в.месяц, data$группа, mean, na.rm = TRUE)
barplot(average_income_by_group, 
        beside = TRUE, 
        col = c("lightgreen", "lightcoral"), 
        names.arg = c("Группа 1", "Группа 2"),
        main = "Средний доход по группе", 
        ylab = "Средний доход в месяц", 
        ylim = c(0, max(average_income_by_group)), 
        border = "white")

# 3. Средний доход по профессиональной специализации (1, 2, 3, 4)
average_income_by_specialization <- tapply(data$средний.доход.в.месяц, 
                                           data$профессиональная.специализация..насколько.тесно.профессия.клиента.связана.с.Интернет., 
                                           mean, na.rm = TRUE)
barplot(average_income_by_specialization, 
        beside = TRUE, 
        col = c("lightblue", "lightgreen", "lightyellow", "lightpink"), 
        names.arg = c("Специализация 1", "Специализация 2", "Специализация 3", "Специализация 4"),
        main = "Средний доход по специализации", 
        ylab = "Средний доход в месяц", 
        ylim = c(0, max(average_income_by_specialization)),
        border = "white")
# 4. Средний доход по степени активности в опросах (высокая, низкая, средняя)
average_income_by_activity <- tapply(data$средний.доход.в.месяц, 
                                     data$степень.активности..участие.в.Интернет.опросах....качественная.оценка, 
                                     mean, na.rm = TRUE)
barplot(average_income_by_activity, 
        beside = TRUE, 
        col = c("lightblue", "lightgreen", "lightcoral"), 
        names.arg = c("высокая", "средняя", "низкая"),
        main = "Средний доход по активности", 
        ylab = "Средний доход в месяц", 
        ylim = c(0, max(average_income_by_activity)),
        border = "white")

# Возвращаем настройку графиков на стандартное отображение (1 график)
par(mfrow = c(1, 1))


# Диаграмма размаха для возраста в зависимости от пола
par(mfrow = c(2, 2))  
boxplot(возраст ~ пол, 
        data = data, 
        main = "Диаграмма размаха: Возраст от пола", 
        ylab = "Возраст", 
        xlab = "Пол", 
        col = c("lightblue", "lightpink"),  # Цвета для мужчин и женщин
        border = "black")

# Диаграмма размаха для возраста в зависимости от группы (1, 2)
boxplot(возраст ~ группа, 
        data = data, 
        main = "Диаграмма размаха: Возраст от группы", 
        ylab = "Возраст", 
        xlab = "Группа", 
        col = c("lightgreen", "lightyellow"),  # Цвета для группы 1 и 2
        border = "black")

# Диаграмма размаха для возраста в зависимости от профессиональной специализации
boxplot(возраст ~ профессиональная.специализация..насколько.тесно.профессия.клиента.связана.с.Интернет., 
        data = data, 
        main = "Диаграмма размаха: Возраст от специализации", 
        ylab = "Возраст", 
        xlab = "Профессиональная специализация", 
        col = rainbow(4),  # 4 цвета для 4 специализаций
        border = "black")

# Диаграмма размаха для возраста в зависимости от степени активности в опросах
boxplot(возраст ~ степень.активности..участие.в.Интернет.опросах....качественная.оценка, 
        data = data, 
        main = "Диаграмма размаха: Возраст от степени активности", 
        ylab = "Возраст", 
        xlab = "Степень активности", 
        col = c("lightblue", "lightgreen", "lightcoral"),  # Цвета для высокой, средней и низкой активности
        border = "black")
par(mfrow = c(1, 1))

# Настройка области графиков для отображения пяти графиков (2 строки и 3 столбца)
par(mfrow = c(2, 3))  # 2 строки и 3 столбца

# Гистограммы для количественных признаков
# Гистограмма для возраста
hist(data$возраст, 
     main = "Гистограмма возраста", 
     xlab = "Возраст", 
     col = "lightblue",     
     breaks = 10) 

# Гистограмма для среднего дохода в месяц
hist(data$средний.доход.в.месяц, 
     main = "Гистограмма дохода", 
     xlab = "Средний доход в месяц", 
     col = "lightgreen",    
     breaks = 10)  

# Гистограмма для стажа работы
hist(data$стаж.работы.в.сети.Интернет, 
     main = "Гистограмма стажа работы", 
     xlab = "Стаж работы", 
     col = "lightcoral",     
     breaks = 10)  

# Гистограмма для количества просмотренных страниц
hist(data$среднее.количество.просматривемых.страниц.в.месяц, 
     main = "Гистограмма просмотров", 
     xlab = "Количество просмотренных страниц", 
     col = "lightyellow",     
     breaks = 10)  

# Гистограмма для степени активности (бальная)
hist(data$степень.активности..участие.в.Интернет.опросах....балльная.оценка, 
     main = "Гистограмма активности (бальная)", 
     xlab = "Степень активности", 
     col = "lightpink",     
     breaks = 10)  

# Возвращаем настройки к одному графику на окно
par(mfrow = c(1, 1)) 


# Матричный график для количественных переменных
pairs(~ возраст + средний.доход.в.месяц + стаж.работы.в.сети.Интернет + среднее.количество.просматривемых.страниц.в.месяц + степень.активности..участие.в.Интернет.опросах....балльная.оценка, 
      data = data, 
      main = "Матричный график количественных переменных", 
      pch = 21,                  
      bg = "lightblue",           
      col = "darkblue")           

#Корреляционный анализ

# Фишера
# Переменные для анализа
var <- c('пол', 'степень.активности..участие.в.Интернет.опросах....качественная.оценка', 'профессиональная.специализация..насколько.тесно.профессия.клиента.связана.с.Интернет.')

# Функция для обработки данных и выполнения тестов
perform_tests <- function(data_group, var) {
  for (i in 1:(length(var) - 1)) {
    for (j in (i + 1):length(var)) {
      # Выбираем пары переменных
      var1 <- var[i]
      var2 <- var[j]
      
      # Создание таблицы сопряжённости
      table_data <- table(data_group[[var1]], data_group[[var2]])
      
      # Выполнение тестов χ² и Фишера
      chi_test <- chisq.test(table_data)
      fisher_test <- fisher.test(table_data)
      
      # Вывод результатов
      print(paste("Анализ переменных:", var1, "и", var2))
      print("Тест χ²:")
      print(chi_test)
      print("Тест Фишера:")
      print(fisher_test)
    }
  }
}

# Перебор по группам и выполнение тестов для первой и второй группы
print("Результаты для первой группы")
perform_tests(data_group1, var)

print("Результаты для второй группы")
perform_tests(data_group2, var)




# ANOVA и критерий Краскела-Уоллиса
anova_result <- aov(возраст ~ пол, data = data)
summary(anova_result)
anova_result <- aov(средний.доход.в.месяц ~ пол, data = data)
summary(anova_result)
anova_result <- aov(стаж.работы.в.сети.Интернет ~ пол, data = data)
summary(anova_result)
anova_result <- aov(среднее.количество.просматривемых.страниц.в.месяц ~ пол, data = data)
summary(anova_result)
anova_result <- aov(степень.активности..участие.в.Интернет.опросах....балльная.оценка ~ пол, data = data)
summary(anova_result)

kruskal_test <- kruskal.test(возраст ~ пол, data = data)
print(kruskal_test)
kruskal_test <- kruskal.test(средний.доход.в.месяц ~ пол, data = data)
print(kruskal_test)
kruskal_test <- kruskal.test(стаж.работы.в.сети.Интернет ~ пол, data = data)
print(kruskal_test)
kruskal_test <- kruskal.test(среднее.количество.просматривемых.страниц.в.месяц ~ пол, data = data)
print(kruskal_test)
kruskal_test <- kruskal.test(степень.активности..участие.в.Интернет.опросах....балльная.оценка ~ пол, data = data)
print(kruskal_test)

# Загрузка необходимых библиотек
library(ggm)
library(corrplot)
data_group1 <- subset(data, группа == 1)
data_group2 <- subset(data, группа == 2)
# Создание таблиц с числовыми переменными для первой и второй групп
M1 <- data_group1[, unlist(lapply(data_group1, is.numeric))]
M2 <- data_group2[, unlist(lapply(data_group2, is.numeric))]


# Коэффициенты корреляции для первой группы
N1_group1 <- cor(M1, use="pairwise.complete.obs")  # Коэффициенты Пирсона
N2_group1 <- cor(M1, use="pairwise.complete.obs", method="spearman")  # Коэффициенты Спирмена
N3_group1 <- cor(M1, use="pairwise.complete.obs", method="kendall")  # Коэффициенты Кендалла

# Коэффициенты корреляции для второй группы
N1_group2 <- cor(M2, use="pairwise.complete.obs")  # Коэффициенты Пирсона
N2_group2 <- cor(M2, use="pairwise.complete.obs", method="spearman")  # Коэффициенты Спирмена
N3_group2 <- cor(M2, use="pairwise.complete.obs", method="kendall")  # Коэффициенты Кендалла

# Вывод коэффициентов корреляции
print("Корреляция Пирсона для первой группы")
print(N1_group1)

print("Корреляция Спирмена для первой группы")
print(N2_group1)

print("Корреляция Кендалла для первой группы")
print(N3_group1)

print("Корреляция Пирсона для второй группы")
print(N1_group2)

print("Корреляция Спирмена для второй группы")
print(N2_group2)

print("Корреляция Кендалла для второй группы")
print(N3_group2)

# Графическое представление для первой группы
col <- colorRampPalette(c("#BB4444", "#EE9988", "#FFFFFF", "#77AADD", "#4477AA"))
corrplot(N1_group1, method="color", col=NULL, 
         type="upper", order="hclust", 
         addCoef.col = "black", tl.col="black", tl.srt=45,
         sig.level = 0.01, insig = "blank", diag=FALSE)

# Графическое представление для второй группы
corrplot(N1_group2, method="color", col=NULL, 
         type="upper", order="hclust", 
         addCoef.col = "black", tl.col="black", tl.srt=45,
         sig.level = 0.01, insig = "blank", diag=FALSE)

library(ggm)
# Для расчета частных коэффициентов корреляции
pcor_group1 <- pcor(c(3, 4, 1, 2, 5), cov(M1))
pcor_group2 <- pcor(c(3, 4, 1, 2, 5), cov(M2))

# Вывод частных коэффициентов
print("Частный коэффициент корреляции для первой группы:")
print(pcor_group1)

print("Частный коэффициент корреляции для второй группы:")
print(pcor_group2)


library(GGally)
ggpairs(data, columns = 2:10, aes(color = группа,alpha = 0.5))



