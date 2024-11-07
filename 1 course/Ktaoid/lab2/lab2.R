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
# Используем линейную модель тренда
t <- 1:length(tsData)
y <- as.numeric(tsData)
data1 <- data.frame(y=y, t=t)
model1 <- lm(y ~ t, data=data1) # Линейная регрессия
summary_model1 <- summary(model1)
print(summary_model1)

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
r_squared <- summary_model1$r.squared

# Вывод значений для таблицы
cat("Минимальный остаток (Min error):", min_error, "\n")
cat("Максимальный остаток (Max error):", max_error, "\n")
cat("Средняя ошибка (Mean error):", mean_error, "\n")
cat("СКО ошибки (Std. dev.):", std_dev, "\n")
cat("Средняя абсолютная ошибка (Mean absolute error):", mean_absolute_error, "\n")
cat("Средняя ошибка в процентах (Mean percentage error):", mean_percentage_error, "\n")
cat("Средняя абсолютная ошибка в процентах (Mean absolute percentage error):", mean_absolute_percentage_error, "\n")
cat("Средний квадрат ошибки (Root mean squared error):", root_mean_squared_error, "\n")
cat("Коэффициент детерминации (R-squared):", r_squared, "\n")

# График исходного ряда с наложением тренда
plot(t, y, main="Исходный ряд с трендовой моделью (Модель 1)")
abline(model1, col="red")

# Остатки Модели 1 и их анализ
residuals_model1 <- residuals(model1)
plot(residuals_model1, main="Остатки Модели 1")
Acf(residuals_model1, main="ACF остатков Модели 1")
Pacf(residuals_model1, main="PACF остатков Модели 1")
spec.pgram(residuals_model1, detrend=FALSE, log="no", main="Периодограмма остатков Модели 1")

# 2.3.2 Идентификация сезонной составляющей (Модель 2)
# Добавляем сезонную составляющую
model2 <- tslm(tsData ~ trend + season)
summary_model2 <- summary(model2)
print(summary_model2)

min_error <- min(residuals(model2))
max_error <- max(residuals(model2))
mean_error <- mean(residuals(model2))
std_dev <- sd(residuals(model2))
mean_absolute_error <- mean(abs(residuals(model2)))
mean_percentage_error <- mean(100 * residuals(model2) / y)
mean_absolute_percentage_error <- mean(100 * abs(residuals(model2)) / y)
root_mean_squared_error <- sqrt(mean(residuals(model2)^2))
r_squared <- summary_model2$r.squared

# Вывод значений для таблицы
cat("Минимальный остаток (Min error):", min_error, "\n")
cat("Максимальный остаток (Max error):", max_error, "\n")
cat("Средняя ошибка (Mean error):", mean_error, "\n")
cat("СКО ошибки (Std. dev.):", std_dev, "\n")
cat("Средняя абсолютная ошибка (Mean absolute error):", mean_absolute_error, "\n")
cat("Средняя ошибка в процентах (Mean percentage error):", mean_percentage_error, "\n")
cat("Средняя абсолютная ошибка в процентах (Mean absolute percentage error):", mean_absolute_percentage_error, "\n")
cat("Средний квадрат ошибки (Root mean squared error):", root_mean_squared_error, "\n")
cat("Коэффициент детерминации (R-squared):", r_squared, "\n")

# Оценка точности модели 2
accuracy_model2 <- accuracy(fitted(model2), tsData)
print("Точность Модели 2:")
print(accuracy_model2)

# График исходного ряда с наложением тренда и сезонной составляющей
plot(tsData, main="Исходный ряд с трендовой и сезонной моделью (Модель 2)")
lines(fitted(model2), col="blue")

# Остатки Модели 2 и их анализ
residuals_model2 <- residuals(model2)
plot(residuals_model2, main="Остатки Модели 2")
Acf(residuals_model2, main="ACF остатков Модели 2")
Pacf(residuals_model2, main="PACF остатков Модели 2")
spec.pgram(residuals_model2, detrend=FALSE, log="no", main="Периодограмма остатков Модели 2")

# 2.3.3 Идентификация авторегрессионной составляющей (Модель 3)
# Добавляем авторегрессионную модель, если в остатках присутствует корреляция
model3 <- auto.arima(tsData, seasonal=TRUE)
summary_model3 <- summary(model3)
print(summary_model3)

min_error <- min(residuals(model3))
max_error <- max(residuals(model3))
mean_error <- mean(residuals(model3))
std_dev <- sd(residuals(model3))
mean_absolute_error <- mean(abs(residuals(model3)))
mean_percentage_error <- mean(100 * residuals(model3) / y)
mean_absolute_percentage_error <- mean(100 * abs(residuals(model3)) / y)
root_mean_squared_error <- sqrt(mean(residuals(model3)^2))
r_squared <- summary_model3$r.squared

# Вывод значений для таблицы
cat("Минимальный остаток (Min error):", min_error, "\n")
cat("Максимальный остаток (Max error):", max_error, "\n")
cat("Средняя ошибка (Mean error):", mean_error, "\n")
cat("СКО ошибки (Std. dev.):", std_dev, "\n")
cat("Средняя абсолютная ошибка (Mean absolute error):", mean_absolute_error, "\n")
cat("Средняя ошибка в процентах (Mean percentage error):", mean_percentage_error, "\n")
cat("Средняя абсолютная ошибка в процентах (Mean absolute percentage error):", mean_absolute_percentage_error, "\n")
cat("Средний квадрат ошибки (Root mean squared error):", root_mean_squared_error, "\n")
cat("Коэффициент детерминации (R-squared):", r_squared, "\n")

# Оценка точности модели 3
accuracy_model3 <- accuracy(fitted(model3), tsData)
print("Точность Модели 3:")
print(accuracy_model3)

# График исходного ряда с наложением Модели 3 (ARIMA с сезонностью)
plot(tsData, main="Исходный ряд с моделью (Модель 3)")
lines(fitted(model3), col="purple")

# Остатки Модели 3 и их анализ
residuals_model3 <- residuals(model3)
plot(residuals_model3, main="Остатки Модели 3")
Acf(residuals_model3, main="ACF остатков Модели 3")
Pacf(residuals_model3, main="PACF остатков Модели 3")
spec.pgram(residuals_model3, detrend=FALSE, log="no", main="Периодограмма остатков Модели 3")

# 2.4 Сравнение моделей и прогноз на 3 шага вперед на основе Модели 3
print("Сравнение точности моделей:")
print(accuracy_model1)
print(accuracy_model2)
print(accuracy_model3)

# Прогноз на 3 шага вперед на основе модели 3
forecast_model3 <- forecast(model3, h=3)
print("Прогноз на 3 шага вперед:")
print(forecast_model3)
plot(forecast_model3)
