error id: `<none>`.
file:///C:/labs/MAGISTER/1%20course/TRPO/Lab-2-1/lab1/src/main/scala/my/scalafx/Main.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -scalafx/Includes.Scene.
	 -scalafx/Includes.Scene#
	 -scalafx/Includes.Scene().
	 -scalafx/scene/Scene.
	 -scalafx/scene/Scene#
	 -scalafx/scene/Scene().
	 -scalafx/scene/control/Scene.
	 -scalafx/scene/control/Scene#
	 -scalafx/scene/control/Scene().
	 -scalafx/scene/layout/Scene.
	 -scalafx/scene/layout/Scene#
	 -scalafx/scene/layout/Scene().
	 -Scene.
	 -Scene#
	 -Scene().
	 -scala/Predef.Scene.
	 -scala/Predef.Scene#
	 -scala/Predef.Scene().
offset: 158
uri: file:///C:/labs/MAGISTER/1%20course/TRPO/Lab-2-1/lab1/src/main/scala/my/scalafx/Main.scala
text:
```scala
package my.scalafx

import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Sce@@ne
import scalafx.scene.control._
import scalafx.scene.layout._
import scalafx.stage.FileChooser
import scalafx.event.ActionEvent
import java.io.IOException
import scalafx.scene.control.Alert.AlertType
import scalafx.geometry.{Insets, Pos}

object Main extends JFXApp3 {

  private var currentFraction1: Fraction = Fraction.of(0, 0, 1)
  private var currentFraction2: Fraction = Fraction.of(0, 0, 1)
  private var resultFraction: Fraction = Fraction.of(0, 0, 1)

  override def start(): Unit = {
    stage = new PrimaryStage {
      title = "Калькулятор дробей"
      scene = new Scene(600, 400) {
        root = createMainLayout()
      }
    }
  }

  private def createMainLayout(): BorderPane = {
    val borderPane = new BorderPane()

    // Создаем верхнюю панель с вводом дробей
    val inputPanel = new GridPane {
      hgap = 10
      vgap = 10
      padding = Insets(10)
    }

    // Поля для первой дроби
    val frac1Whole = new TextField { promptText = "Целая часть" }
    val frac1Num = new TextField { promptText = "Числитель" }
    val frac1Den = new TextField { promptText = "Знаменатель" }
    val frac1Label = new Label("Дробь 1:")

    // Поля для второй дроби
    val frac2Whole = new TextField { promptText = "Целая часть" }
    val frac2Num = new TextField { promptText = "Числитель" }
    val frac2Den = new TextField { promptText = "Знаменатель" }
    val frac2Label = new Label("Дробь 2:")

    // Кнопка установки значений
    val setButton = new Button("Установить дроби") {
      onAction = { _ =>
        try {
          currentFraction1 = Fraction.of(
            frac1Whole.text.value.toInt,
            frac1Num.text.value.toInt,
            frac1Den.text.value.toInt
          )
          currentFraction2 = Fraction.of(
            frac2Whole.text.value.toInt,
            frac2Num.text.value.toInt,
            frac2Den.text.value.toInt
          )
          showAlert("Дроби установлены", AlertType.Information)
        } catch {
          case e: Exception => showAlert(s"Ошибка: ${e.getMessage}", AlertType.Error)
        }
      }
    }

    // Добавляем элементы на панель
    inputPanel.add(frac1Label, 0, 0)
    inputPanel.add(frac1Whole, 1, 0)
    inputPanel.add(new Label("и"), 2, 0)
    inputPanel.add(frac1Num, 3, 0)
    inputPanel.add(new Label("/"), 4, 0)
    inputPanel.add(frac1Den, 5, 0)

    inputPanel.add(frac2Label, 0, 1)
    inputPanel.add(frac2Whole, 1, 1)
    inputPanel.add(new Label("и"), 2, 1)
    inputPanel.add(frac2Num, 3, 1)
    inputPanel.add(new Label("/"), 4, 1)
    inputPanel.add(frac2Den, 5, 1)

    inputPanel.add(setButton, 0, 2, 6, 1)

    // Панель операций
    val operationsPanel = new HBox(10) {
      padding = Insets(10)
      alignment = Pos.Center
    }

    val addButton = new Button("+") {
      onAction = { _ =>
        resultFraction = currentFraction1.addition(currentFraction2)
        updateResult()
      }
    }

    val subButton = new Button("-") {
      onAction = { _ =>
        resultFraction = currentFraction1.subtraction(currentFraction2)
        updateResult()
      }
    }

    val mulButton = new Button("*") {
      onAction = { _ =>
        resultFraction = currentFraction1.multiplication(currentFraction2)
        updateResult()
      }
    }

    val divButton = new Button("/") {
      onAction = { _ =>
        resultFraction = currentFraction1.division(currentFraction2)
        updateResult()
      }
    }

    operationsPanel.children.addAll(addButton, subButton, mulButton, divButton)

    // Панель результата
    val resultPanel = new VBox(10) {
      padding = Insets(10)
      alignment = Pos.Center
    }

    val resultLabel = new Label("Результат: ")
    val resultText = new Label("")

    def updateResult(): Unit = {
      resultText.text = resultFraction.toString
    }

    resultPanel.children.addAll(resultLabel, resultText)

    // Панель файловых операций
    val filePanel = new HBox(10) {
      padding = Insets(10)
      alignment = Pos.Center
    }

    val saveTextButton = new Button("Сохранить в текстовый файл") {
      onAction = { _ =>
        saveToFile(false)
      }
    }

    val saveBinaryButton = new Button("Сохранить в двоичный файл") {
      onAction = { _ =>
        saveToFile(true)
      }
    }

    val loadTextButton = new Button("Загрузить из текстового файла") {
      onAction = { _ =>
        loadFromFile(false)
      }
    }

    val loadBinaryButton = new Button("Загрузить из двоичного файла") {
      onAction = { _ =>
        loadFromFile(true)
      }
    }

    filePanel.children.addAll(saveTextButton, saveBinaryButton, loadTextButton, loadBinaryButton)

    // Собираем основной интерфейс
    borderPane.top = inputPanel
    borderPane.center = operationsPanel
    borderPane.bottom = resultPanel
    borderPane.bottom = new VBox(10, resultPanel, filePanel) {
      padding = Insets(10)
      alignment = Pos.Center
    }

    borderPane
  }

  private def saveToFile(binary: Boolean): Unit = {
    val fileChooser = new FileChooser {
      title = if (binary) "Сохранить в двоичный файл" else "Сохранить в текстовый файл"
      extensionFilters.add(
        new FileChooser.ExtensionFilter(
          if (binary) "Binary Files (*.bin)" else "Text Files (*.txt)",
          if (binary) "*.bin" else "*.txt"
        )
      )
    }

    val file = fileChooser.showSaveDialog(stage)
    if (file != null) {
      try {
        if (binary) {
          resultFraction.writeToFileBinary(file.getAbsolutePath)
        } else {
          resultFraction.writeToFileChar(file.getAbsolutePath)
        }
        showAlert("Файл успешно сохранён", AlertType.Information)
      } catch {
        case e: IOException => showAlert(s"Ошибка сохранения: ${e.getMessage}", AlertType.Error)
      }
    }
  }

  private def loadFromFile(binary: Boolean): Unit = {
    val fileChooser = new FileChooser {
      title = if (binary) "Загрузить из двоичного файла" else "Загрузить из текстового файла"
      extensionFilters.add(
        new FileChooser.ExtensionFilter(
          if (binary) "Binary Files (*.bin)" else "Text Files (*.txt)",
          if (binary) "*.bin" else "*.txt"
        )
      )
    }

    val file = fileChooser.showOpenDialog(stage)
    if (file != null) {
      try {
        val fraction = if (binary) {
          Fraction.readFromBinaryFile(file.getAbsolutePath)
        } else {
          Fraction.readFromTextFile(file.getAbsolutePath)
        }
        resultFraction = fraction
        updateResult()
        showAlert("Файл успешно загружен", AlertType.Information)
      } catch {
        case e: IOException => showAlert(s"Ошибка загрузки: ${e.getMessage}", AlertType.Error)
        case e: NumberFormatException => showAlert("Неверный формат файла", AlertType.Error)
      }
    }
  }

  private def showAlert(message: String, alertType: AlertType): Unit = {
    new Alert(alertType) {
      title = "Сообщение"
      headerText = message
    }.showAndWait()
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.