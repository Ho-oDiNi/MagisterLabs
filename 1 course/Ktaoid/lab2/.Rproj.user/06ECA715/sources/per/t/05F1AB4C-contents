# Загрузка библиотек
library(forecast)
library(tseries)

# Загрузка данных
data <- read.csv("data.csv", header=TRUE, sep=";", check.names=F, fileEncoding="Windows-1251")
tsData <- ts(data[15], frequency=12) # Загрузка 12 варианта временного ряда

# 2.2 Построение графика временного ряда и его декомпозиция
plot.ts(tsData, main="Временной ряд")
decomp <- decompose(tsData)
plot(decomp)

# Построение автокорреляционной и частной автокорреляционной функций
Acf(tsData, main="ACF Временного ряда")
Pacf(tsData, main="PACF Временного ряда")

# 2.3.1 Идентификация тренда (Модель 1)
# Используем модель тренда
t <- 1:length(tsData)
y <- as.numeric(tsData)
data1 <- data.frame(y=y, t=t)
model1 <- nls(y ~ a0+a1*t+a2*t^2, data=data1, start = c(a0=1,a1=1,a2=1))
summary_model1 <- summary(model1)


# Оценка точности модели
accuracy_model1 <- accuracy(fitted(model1), tsData)
print("Точность Модели 1:")
print(accuracy_model1)

min_error <- min(residuals(model1))
max_error <- max(residuals(model1))
mean_error <- mean(residuals(model1))
std_dev <- sd(residuals(model1))
mean_absolute_error <- mean(abs(residuals(model1)))
mean_percentage_error <- mean(100 * residuals(model1) / y)
mean_absolute_percentage_error <- mean(100 * abs(residuals(model1)) / y)
root_mean_squared_error <- sqrt(mean(residuals(model1)^2))

# Вывод значений для таблицы
cat("Минимальный остаток (Min error):", min_error, "\n")
cat("Максимальный остаток (Max error):", max_error, "\n")
cat("Средняя ошибка (Mean error):", mean_error, "\n")
cat("СКО ошибки (Std. dev.):", std_dev, "\n")
cat("Средняя абсолютная ошибка (Mean absolute error):", mean_absolute_error, "\n")
cat("Средняя ошибка в процентах (Mean percentage error):", mean_percentage_error, "\n")
cat("Средняя абсолютная ошибка в процентах (Mean absolute percentage error):", mean_absolute_percentage_error, "\n")
cat("Средний квадрат ошибки (Root mean squared error):", root_mean_squared_error, "\n")

# График исходного ряда с наложением тренда
plot(t, y, main="Исходный ряд с трендовой моделью (Модель 1)")
lines(t, predict(model1), col="red")

# Остатки Модели 1 и их анализ
residuals_model1 <- residuals(model1)
plot(residuals_model1, main="Остатки Модели 1")
Acf(residuals_model1, main="ACF остатков Модели 1")
Pacf(residuals_model1, main="PACF остатков Модели 1")
residuals_model12 <- ts(residuals_model1, frequency = 12)
spec.pgram(residuals_model12, detrend=FALSE, log="no", main="Периодограмма остатков Модели 1")

# 2.3.2 Идентификация сезонной составляющей (Модель 2)
# Добавляем сезонную составляющую
# model2 <- tslm(tsData ~ trend + season)
model2 <- nls(y ~ a0+a1*t+a2*t^2 + c0*sin(2*pi*t/12) + b0*cos(2*pi*t/12)
              + c1* sin(2*pi*t/6) + b1*cos(2*pi*t/6),
              data=data1, start = c(a0=1,a1=1,a2=1,c0=1,b0=1,c1=1,b1=1))
summary_model2 <- summary(model2)

min_error <- min(residuals(model2))
max_error <- max(residuals(model2))
mean_error <- mean(residuals(model2))
std_dev <- sd(residuals(model2))
mean_absolute_error <- mean(abs(residuals(model2)))
mean_percentage_error <- mean(100 * residuals(model2) / y)
mean_absolute_percentage_error <- mean(100 * abs(residuals(model2)) / y)
root_mean_squared_error <- sqrt(mean(residuals(model2)^2))

# Вывод значений для таблицы
cat("Минимальный остаток (Min error):", min_error, "\n")
cat("Максимальный остаток (Max error):", max_error, "\n")
cat("Средняя ошибка (Mean error):", mean_error, "\n")
cat("СКО ошибки (Std. dev.):", std_dev, "\n")
cat("Средняя абсолютная ошибка (Mean absolute error):", mean_absolute_error, "\n")
cat("Средняя ошибка в процентах (Mean percentage error):", mean_percentage_error, "\n")
cat("Средняя абсолютная ошибка в процентах (Mean absolute percentage error):", mean_absolute_percentage_error, "\n")
cat("Средний квадрат ошибки (Root mean squared error):", root_mean_squared_error, "\n")


# Оценка точности модели 2
accuracy_model2 <- accuracy(fitted(model2), tsData)
print("Точность Модели 2:")
print(accuracy_model2)

# График исходного ряда с наложением тренда и сезонной составляющей
plot(data$`вар 12`, type = 'l')
lines(fitted(model2), col='blue')

# Остатки Модели 2 и их анализ
residuals_model2 <- residuals(model2)
plot(residuals_model2, main="Остатки Модели 2")
Acf(residuals_model2, main="ACF остатков Модели 2")
Pacf(residuals_model2, main="PACF остатков Модели 2")
spec.pgram(residuals_model2, detrend=FALSE, log="no", main="Периодограмма остатков Модели 2")

# 2.4 Сравнение моделей и прогноз на 3 шага вперед на основе Модели 2
print("Сравнение точности моделей:")
print(accuracy_model1)
print(accuracy_model2)

# Прогноз на 3 шага вперед на основе модели 2
#forecast_model2 <- forecast(model2, h=3)
#print("Прогноз на 3 шага вперед:")
#print(forecast_model2)
#plot(forecast_model2)

predict1 <- predict(model2,newdata = data.frame(t = c(193,194,195)))
fullpredicted1 <- c(fitted(model2), predict1)
plot(data$`вар 12`, type = 'l')
lines(fullpredicted1, col='blue')
lines(fitted(model2), col='red')