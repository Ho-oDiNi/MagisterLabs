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

# Радиальная диаграмма для качественного признака packages(plotrix)
survey_table <- table(data$степень.активности..участие.в.Интернет.опросах....качественная.оценка, useNA = "no")
print(survey_table)
library(plotrix)
survey_values <- as.numeric(survey_table)
survey_labels <- names(survey_table)
# Построение радиальной диаграммы
radial.plot(survey_values, 
            labels = survey_labels, 
            main = "Радиальная диаграмма по степени активности в опросах",
            radial.lim = c(0, max(survey_values)),  # Установка пределов радиуса
            rp.type = "p",
            line.col = "blue",    # Цвет линий
            point.col = "red",    # Цвет точек
            show.grid.labels = TRUE)  # Показывать подписи сетки

# Категориальная радиальная диаграмма по групповым переменным
library(plotrix)
# Создаем таблицу частот по профессиональной специализации и полу
category_table <- table(data$профессиональная.специализация..насколько.тесно.профессия.клиента.связана.с.Интернет., 
                        data$пол)
category_values_male <- as.numeric(category_table[, "1"])
category_values_female <- as.numeric(category_table[, "2"])
# Определение меток для осей (профессиональная специализация)
labels <- rownames(category_table)
# Построение радиальной диаграммы с учетом групповой переменной
radial.plot(category_values_male, 
            labels = labels, 
            main = "Категориальная радиальная диаграмма по профессиональной специализации и полу",
            radial.lim = c(0, max(category_values_male, category_values_female)),  # Установка пределов радиуса
            rp.type = "p",  # Тип графика "p" для точек
            line.col = "blue",   
            point.col = "blue")  
# Добавляем данные для женщин на тот же график
radial.plot(category_values_female, 
            add = TRUE,  # Добавляем к существующему графику
            line.col = "red",    
            point.col = "red")   
legend("topright", legend = c("Мужской", "Женский"), col = c("blue", "red"), lty = 1, pch = 16)


# Категориальная столбиковая диаграмма для количественного признака в зависимости от групповой переменной
# Построение столбиковой диаграммы среднего дохода по полу
barplot(tapply(data$средний.доход.в.месяц, data$пол, mean, na.rm = TRUE),
        main = "Средний доход по полу", 
        xlab = "Пол", 
        ylab = "Средний доход в месяц",
        names.arg = c("Мужчины", "Женщины"),  # Если 1 - мужчины, 2 - женщины
        col = c("skyblue", "lightpink"),  
        ylim = c(0, max(tapply(data$средний.доход.в.месяц, data$пол, mean, na.rm = TRUE)) * 1.1))  

#Диаграмма размаха для количественного признака (возраст в зависимости от пола):
boxplot(возраст ~ пол, data = data, 
        main = "Диаграмма размаха возраста по полу", 
        xlab = "Пол", 
        ylab = "Возраст", 
        names = c("Мужчины", "Женщины"),  # Если 1 - мужчины, 2 - женщины
        col = c("skyblue", "lightpink"))  # Цвет для каждого пола


#Гистограммы для количественных признаков:
# Настройка области графиков для отображения двух графиков в одном окне
par(mfrow = c(1, 2))
# Построение первой гистограммы для возраста
hist(data$возраст, 
     main = "Гистограмма возраста", 
     xlab = "Возраст",
     col = "lightblue",     
     breaks = 10)     
# Построение второй гистограммы для среднего дохода в месяц
hist(data$средний.доход.в.месяц, 
     main = "Гистограмма дохода", 
     xlab = "Средний доход в месяц",
     col = "lightgreen",    
     breaks = 10)           
# Возвращаем настройки к одному графику на окно
par(mfrow = c(1, 1))

# Матричный график для количественных переменных
pairs(~ возраст + средний.доход.в.месяц + стаж.работы.в.сети.Интернет, 
      data = data, 
      main = "Матричный график количественных переменных", 
      pch = 21,                  
      bg = "lightblue",           
      col = "darkblue")           

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
library(ppcor)
# Рассчитываем матрицу корреляции Пирсона для первой группы
cor_matrix_group1 <- cor(data_group1[, c("возраст", "средний.доход.в.месяц", "стаж.работы.в.сети.Интернет")], use = "complete.obs")
# Находим пару переменных с максимальным модулем коэффициента корреляции
max_corr_vars_group1 <- which(abs(cor_matrix_group1) == max(abs(cor_matrix_group1[upper.tri(cor_matrix_group1)])), arr.ind = TRUE)
# Повторяем для второй группы
cor_matrix_group2 <- cor(data_group2[, c("возраст", "средний.доход.в.месяц", "стаж.работы.в.сети.Интернет")], use = "complete.obs")
max_corr_vars_group2 <- which(abs(cor_matrix_group2) == max(abs(cor_matrix_group2[upper.tri(cor_matrix_group2)])), arr.ind = TRUE)
# Частный коэффициент корреляции для первой группы
var1_group1 <- names(data_group1)[max_corr_vars_group1[1]]
var2_group1 <- names(data_group1)[max_corr_vars_group1[2]]
pcor_result_group1 <- pcor.test(data_group1[[var1_group1]], data_group1[[var2_group1]], data_group1$стаж.работы.в.сети.Интернет)
# Частный коэффициент корреляции для второй группы
var1_group2 <- names(data_group2)[max_corr_vars_group2[1]]
var2_group2 <- names(data_group2)[max_corr_vars_group2[2]]
pcor_result_group2 <- pcor.test(data_group2[[var1_group2]], data_group2[[var2_group2]], data_group2$стаж.работы.в.сети.Интернет)
# Вывод результатов
print("Частный коэффициент корреляции для первой группы")
print(pcor_result_group1)
print("Частный коэффициент корреляции для второй группы")
print(pcor_result_group2)

library(GGally)
library(psych)
# Выбор количественных переменных
quant_vars <- data[, c("возраст", "средний.доход.в.месяц", "стаж.работы.в.сети.Интернет", 
                       "среднее.количество.просматривемых.страниц.в.месяц", 
                       "степень.активности..участие.в.Интернет.опросах....балльная.оценка")]

# Рассчёт матрицы коэффициентов корреляции Пирсона
cor_matrix <- cor(quant_vars, use = "complete.obs", method = "pearson")

# Функция для теста значимости корреляции (p-value) для каждой пары переменных
cor_test <- corr.test(quant_vars, method = "pearson")

# Вывод матрицы корреляции
print("Матрица коэффициентов корреляции:")
print(cor_matrix)

# Вывод p-значений
print("Матрица значимости (p-values):")
print(cor_test$p)

# Построение графика корреляций с помощью ggpairs (включает распределение и графики корреляций)
ggpairs(quant_vars, 
        upper = list(continuous = wrap("cor", size = 3, color = "blue")), # корреляции в верхней треугольной части
        lower = list(continuous = "smooth"), # линейный тренд в нижней части
        diag = list(continuous = "densityDiag")) # плотность распределений на диагонали


library(corrplot)

# Подготовка данных: выбор количественных переменных
quant_vars <- data[, sapply(data, is.numeric)]

# Расчёт матрицы коэффициентов корреляции Пирсона
cor_matrix <- cor(quant_vars, use = "complete.obs", method = "pearson")

# Устанавливаем параметры графического устройства перед построением графика
par(mar = c(1, 1, 1, 1))  # Уменьшаем отступы для графика

# Визуализация корреляционной матрицы с использованием corrplot
corrplot(cor_matrix, 
         method = "color",        # метод цветового заполнения
         col = colorRampPalette(c("#B44444", "#FFFFFF", "#77AADD"))(200), # цветовая палитра
         addCoef.col = "black",    # цвет значений коэффициентов
         tl.col = "black",         # цвет подписей
         tl.srt = 30,              # уменьшен угол поворота подписей
         number.cex = 0.6,         # уменьшен размер шрифта коэффициентов
         diag = FALSE)             # убирает значения на диагонали


