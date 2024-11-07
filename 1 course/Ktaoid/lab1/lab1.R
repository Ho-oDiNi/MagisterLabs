#загрузка данных
data <- read.table(file = 
     "C:\\labs\\MAGISTER\\1 course\\Ktaoid\\lab1\\datafile.csv",
     header = TRUE, sep = ";", fileEncoding = "Windows-1251",
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
subset(data, возраст > 30)
# структура данных
str(data)
# Просмотр статистики по данным
summary(data)
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
skewness(data$возраст, na.rm = TRUE)
# Эксцесс packages("e1071")
kurtosis(data$возраст, na.rm = TRUE)
# Диаграмма рассеяния возраста и дохода
plot(data$возраст, data$средний.доход.в.месяц, 
     xlab = "Возраст", ylab = "Средний доход в месяц", 
     main = "Диаграмма рассеяния: Возраст vs Средний доход")

# Радиальная диаграмма для качественного признака packages(plotrix)
age_table <- table(data$возраст, useNA = "no")
print(age_table)
library(plotrix)
# Подготовка данных для радиальной диаграммы
# Преобразуем таблицу в вектор и уберем NA
age_values <- as.numeric(age_table)
radial.plot(age_values, 
            labels = names(age_table), 
            main = "Радиальная диаграмма по возрасту",
            radial.lim = c(0, max(age_values)),  # Установка пределов радиуса
            rp.type = "p")

# Категориальная радиальная диаграмма по групповым переменным
library(plotrix)
# Например, профессиональная специализация
category_table <- table(data$профессиональная.специализация..насколько.тесно.профессия.клиента.связана.с.Интернет.)
# Преобразуем таблицу в числовой вектор
category_values <- as.numeric(category_table)
# Построение радиальной диаграммы
radial.plot(category_values, 
            labels = names(category_table), 
            main = "Категориальная радиальная диаграмма по профессиональной специализации",
            radial.lim = c(0, max(category_values)),  # Установка пределов радиуса
            rp.type = "p")  # Тип графика "p" для точек

#Категориальная столбиковая диаграмма для количественного признака в зависимости от групповой переменной
barplot(tapply(data$средний.доход.в.месяц, data$пол, mean, na.rm = TRUE),
        main = "Средний доход по полу", 
        xlab = "Пол", ylab = "Средний доход в месяц")

#Диаграмма размаха для количественного признака (возраст в зависимости от пола):
boxplot(возраст ~ пол, data = data, main = "Диаграмма размаха возраста по полу", 
        xlab = "Пол", ylab = "Возраст", names.arg = c("Мужчины", "Женщины"))

#Гистограммы для количественных признаков:
# Настройка области графиков для отображения двух графиков в одном окне
par(mfrow = c(1, 2))
# Построение первой гистограммы для возраста
hist(data$возраст, 
     main = "Гистограмма возраста", 
     xlab = "Возраст",
     ylab = "Количество наблюдений")
# Построение второй гистограммы для среднего дохода в месяц
hist(data$средний.доход.в.месяц, 
     main = "Гистограмма дохода", 
     xlab = "Средний доход в месяц",
     ylab = "Количество наблюдений")
par(mfrow = c(1, 1))  # Возвращаем настройки к одному графику на окно

# Матричный график для количественных переменных
pairs(~ возраст + средний.доход.в.месяц + стаж.работы.в.сети.Интернет, data = data)

#Корреляционный анализ

# Фишера
# Подмножества для первой и второй группы
data_group1 <- subset(data, группа == 1)
data_group2 <- subset(data, группа == 2)

# Создание таблиц сопряжённости для первой группы
table_data_group1 <- table(data_group1$пол, data_group1$степень.активности..участие.в.Интернет.опросах....качественная.оценка)
chi_test_group1 <- chisq.test(table_data_group1)
fisher_test_group1 <- fisher.test(table_data_group1)

# Создание таблиц сопряжённости для второй группы
table_data_group2 <- table(data_group2$пол, data_group2$степень.активности..участие.в.Интернет.опросах....качественная.оценка)
chi_test_group2 <- chisq.test(table_data_group2)
fisher_test_group2 <- fisher.test(table_data_group2)

# Вывод результатов для первой группы
print("Тест χ² для первой группы")
print(chi_test_group1)
print('Тест Фишера для первой группы')
print(fisher_test_group1)

# Вывод результатов для второй группы
print("Тест χ² для второй группы")
print(chi_test_group2)
print('Тест Фишера для второй группы')
print(fisher_test_group2)

# ANOVA и критерий Краскела-Уоллиса
# влияния пола на средний доход
anova_result <- aov(средний.доход.в.месяц ~ пол, data = data)
summary(anova_result)
kruskal_test <- kruskal.test(среднее.количество.просматривемых.страниц.в.месяц ~ пол, data = data)
print(kruskal_test)

# коэффициенты корреляции Пирсона, Спирмена, Кендалла
# Корреляция Пирсона для первой группы
cor_pearson_group1 <- cor(data_group1$возраст, data_group1$средний.доход.в.месяц, method = "pearson")

# Корреляция Пирсона для второй группы
cor_pearson_group2 <- cor(data_group2$возраст, data_group2$средний.доход.в.месяц, method = "pearson")

# Корреляция Спирмена для первой группы
cor_spearman_group1 <- cor(data_group1$стаж.работы.в.сети.Интернет, data_group1$средний.доход.в.месяц, method = "spearman")

# Корреляция Спирмена для второй группы
cor_spearman_group2 <- cor(data_group2$стаж.работы.в.сети.Интернет, data_group2$средний.доход.в.месяц, method = "spearman")

# Корреляция Кендалла для первой группы
cor_kendall_group1 <- cor(data_group1$степень.активности..участие.в.Интернет.опросах....балльная.оценка, data_group1$возраст, method = "kendall")

# Корреляция Кендалла для второй группы
cor_kendall_group2 <- cor(data_group2$степень.активности..участие.в.Интернет.опросах....балльная.оценка, data_group2$возраст, method = "kendall")

# Вывод результатов
print("Корреляция Пирсона для первой группы")
print(cor_pearson_group1)
print("Корреляция Пирсона для второй группы")
print(cor_pearson_group2)

print("Корреляция Спирмена для первой группы")
print(cor_spearman_group1)
print("Корреляция Спирмена для второй группы")
print(cor_spearman_group2)

print("Корреляция Кендалла для первой группы")
print(cor_kendall_group1)
print("Корреляция Кендалла для второй группы")
print(cor_kendall_group2)

#частный коэффициент корреляции
# Подмножества для первой и второй группы
data_group1 <- subset(data, группа == 1)
data_group2 <- subset(data, группа == 2)

# Частный коэффициент корреляции для первой группы
pcor_result_group1 <- pcor.test(data_group1$возраст, data_group1$средний.доход.в.месяц, data_group1$стаж.работы.в.сети.Интернет)

# Частный коэффициент корреляции для второй группы
pcor_result_group2 <- pcor.test(data_group2$возраст, data_group2$средний.доход.в.месяц, data_group2$стаж.работы.в.сети.Интернет)

# Вывод результатов для первой группы
print("Частный коэффициент корреляции для первой группы")
print(pcor_result_group1)

# Вывод результатов для второй группы
print("Частный коэффициент корреляции для второй группы")
print(pcor_result_group2)

# Выбор количественных переменных
quant_vars <- data[, c("возраст", "средний.доход.в.месяц", "стаж.работы.в.сети.Интернет", 
                       "среднее.количество.просматривемых.страниц.в.месяц", 
                       "степень.активности..участие.в.Интернет.опросах....балльная.оценка")]

# Построение матричной диаграммы
ggpairs(quant_vars)