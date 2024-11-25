library("arules")
library("arulesViz")
data <- read.delim(file = "lab3_10.csv", sep = ",", header
                   = TRUE, row.names = 1)
View(data)
str(data)

dataN <- data[, -11] # Берем данные, исключая столбец Кредит
isCredit <- data$Кредит # Столбец "Кредит"
isCredit[isCredit == 1] <- "С кредитом"
isCredit[isCredit == 0] <- "Без кредита"
View(dataN)

itemsList <- sapply(1:nrow(dataN), function(i) # файл транзакций
  paste(c(isCredit[i], colnames(dataN[i, dataN[i, ] == 1])),
        collapse = ",", sep = "\n"))
head(itemsList)
write(itemsList, file = "basket.csv") # заполняется файл транзакций.
trans <- read.transactions("basket.csv", format = "basket", sep
                           = ",")
inspect(trans)
summary(trans)

itemFrequencyPlot(trans, cex.names = 0.8) #Построение частотной диаграммы транзакций

#Для составления ассоциативных правил с минимальной поддержкой 0.1 и минимальной достоверностью 0.5 для заданных транзакций
rules <- apriori(trans, parameter = list(support = 0.1, confidence =
                                           0.5))
summary(rules)

rulesWithCredit <- subset(rules, subset = rhs
                                            %in% "С кредитом") # транзакции с использованием кредита
plot(rulesWithCredit, method = "paracoord")
plot(head(sort(rulesWithCredit, by = "support"), 10), method =
       "paracoord") # ограничение количества отображаемых правил

plot(rulesWithCredit, method = "graph",
     control = list(nodeCol = grey.colors(10),
                    edgeCol = grey(.7), alpha = 1))

plot(head(sort(rulesWithCredit, by = "support"), 10), method =
       "graph",
     control = list(nodeCol = grey.colors(10),
                    edgeCol = grey(.7), alpha = 1))