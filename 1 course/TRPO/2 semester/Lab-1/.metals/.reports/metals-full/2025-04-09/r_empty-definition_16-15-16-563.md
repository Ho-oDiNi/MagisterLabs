error id: scala/collection/immutable/List#splitAt().
file:///C:/labs/MAGISTER/1%20course/TRPO/Lab-2-1/main.scala
empty definition using pc, found symbol in pc: scala/collection/immutable/List#splitAt().
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -list/splitAt.
	 -list/splitAt#
	 -list/splitAt().
	 -scala/Predef.list.splitAt.
	 -scala/Predef.list.splitAt#
	 -scala/Predef.list.splitAt().
offset: 520
uri: file:///C:/labs/MAGISTER/1%20course/TRPO/Lab-2-1/main.scala
text:
```scala
import java.io.{DataInputStream, DataOutputStream, File, FileInputStream, FileOutputStream, IOException, PrintWriter}
import scala.io.Source
import java.nio.file.{Files, Paths, Path}
import scala.util.{Try, Using}

object FunctionalMergeSort {
  // Обобщенная сортировка слиянием (функциональный стиль)
  def mergeSort[T](list: List[T])(implicit ord: Ordering[T]): List[T] = {
    // Базовый случай
    if (list.length <= 1) list
    else {
      // Рекурсивное разделение
      val (left, right) = list.spli@@tAt(list.length / 2)
      // Рекурсивное слияние
      merge(mergeSort(left), mergeSort(right))
    }
  }

  // Рекурсивное слияние
  private def merge[T](left: List[T], right: List[T])(implicit ord: Ordering[T]): List[T] = 
    (left, right) match {
      case (Nil, _) => right
      case (_, Nil) => left
      case (l :: ls, r :: rs) =>
        if (ord.lteq(l, r)) l :: merge(ls, right)
        else r :: merge(left, rs)
    }

  // Лексикографическая сортировка строк
  def lexicographicSort(strings: List[String]): List[String] = 
    mergeSort(strings)(Ordering.String)

  // Сортировка с пользовательской функцией сравнения
  def sortWithComparator[T](list: List[T])(compare: (T, T) => Boolean): List[T] = {
    implicit val customOrdering: Ordering[T] = Ordering.fromLessThan(compare)
    mergeSort(list)
  }
}

object ImperativeMergeSort {
  // Обобщенная сортировка слиянием (императивный стиль)
  def mergeSort[T](arr: Array[T])(implicit ord: Ordering[T]): Unit = {
    if (arr.length <= 1) return
    
    // Рекурсивное разделение
    val mid = arr.length / 2
    val left = arr.slice(0, mid)
    val right = arr.slice(mid, arr.length)
    
    mergeSort(left)
    mergeSort(right)
    
    // Циклическое слияние
    var (i, j, k) = (0, 0, 0)
    while (i < left.length && j < right.length) {
      if (ord.lteq(left(i), right(j))) {
        arr(k) = left(i)
        i += 1
      } else {
        arr(k) = right(j)
        j += 1
      }
      k += 1
    }
    
    // Однократное слияние оставшихся элементов
    while (i < left.length) {
      arr(k) = left(i)
      i += 1
      k += 1
    }
    
    while (j < right.length) {
      arr(k) = right(j)
      j += 1
      k += 1
    }
  }

  // Сортировка с внешним компаратором
  def sortWithExternalComparator[T](arr: Array[T], compare: (T, T) => Boolean): Unit = {
    implicit val customOrdering: Ordering[T] = Ordering.fromLessThan(compare)
    mergeSort(arr)
  }
}

class Fraction private (private var _wholePart: Int, private var _numerator: Int, private var _denominator: Int) {
  // Геттеры
  def wholePart: Int = _wholePart
  def numerator: Int = _numerator
  def denominator: Int = _denominator

  // Сеттеры с валидацией
  def wholePart_=(value: Int): Unit = {
    if (!Fraction.isValid(value, numerator, denominator)) {
      throw new IllegalArgumentException("Предоставлены некорректные данные")
    }
    _wholePart = value
  }

  def numerator_=(value: Int): Unit = {
    if (!Fraction.isValid(wholePart, value, denominator)) {
      throw new IllegalArgumentException(
        "Предоставлен некорректный числитель, который больше или равен знаменателю")
    }
    _numerator = value
  }

  def denominator_=(value: Int): Unit = {
    if (!Fraction.isValid(wholePart, numerator, value)) {
      throw new IllegalArgumentException(
        "Предоставлен некорректный знаменатель, который меньше или равен числителю")
    }
    _denominator = value
  }

  def addition(fraction: Fraction): Fraction = {
    val currentFractionNumerator = getCorrectNumerator(this)
    val additionalFractionNumerator = getCorrectNumerator(fraction)
    val minDenominator = {
      val nod = getNOD(this.denominator, fraction.denominator)
      (this.denominator * fraction.denominator) / nod
    }
    val commonNumerator = currentFractionNumerator * minDenominator / this.denominator +
      additionalFractionNumerator * minDenominator / fraction.denominator
    val commonNODNumeratorDenominator = getNOD(commonNumerator, minDenominator)
    
    val (simplifiedNum, simplifiedDenom) = if (commonNODNumeratorDenominator != 1) {
      (commonNumerator / commonNODNumeratorDenominator, minDenominator / commonNODNumeratorDenominator)
    } else {
      (commonNumerator, minDenominator)
    }
    
    val currentWholePart = simplifiedNum / simplifiedDenom
    var remainingNumerator = simplifiedNum - currentWholePart * simplifiedDenom
    if (remainingNumerator < 0) {
      remainingNumerator *= -1
    }
    
    Fraction.of(currentWholePart, remainingNumerator, simplifiedDenom)
  }

  def subtraction(fraction: Fraction): Fraction = {
    addition(new Fraction(0, -getCorrectNumerator(fraction), fraction.denominator))
  }

  def multiplication(fraction: Fraction): Fraction = {
    val tmpFraction = getFraction(fraction)
    val currentWholePart = tmpFraction.numerator / tmpFraction.denominator
    var currentNumerator = tmpFraction.numerator - currentWholePart * tmpFraction.denominator
    val nod = getNOD(currentNumerator, tmpFraction.denominator)
    
    if (currentWholePart < 0 && currentNumerator < 0) {
      currentNumerator *= -1
    }
    
    Fraction.of(currentWholePart, currentNumerator / nod, tmpFraction.denominator / nod)
  }

  def division(fraction: Fraction): Fraction = {
    var tmpNumerator = getCorrectNumerator(fraction)
    var tmpDenominator = fraction.denominator
    if (tmpNumerator < 0) {
      tmpNumerator *= -1
      tmpDenominator *= -1
    }
    multiplication(new Fraction(0, tmpDenominator, tmpNumerator))
  }

  @throws[IOException]
  def writeToFileChar(filename: String): Unit = {
    val path = Paths.get(filename)
    Using(Files.newBufferedWriter(path)) { writer =>
      writer.write(this.toString)
    }.get
  }

  @throws[IOException]
  def writeToFileBinary(filename: String): Unit = {
    val path = Paths.get(filename)
    Using(new DataOutputStream(Files.newOutputStream(path))) { dos =>
      dos.writeInt(wholePart)
      dos.writeInt(numerator)
      dos.writeInt(denominator)
    }.get
  }

  override def toString: String = {
    if (numerator == 0) {
      wholePart.toString
    } else if (wholePart == 0) {
      s"$numerator/$denominator"
    } else {
      s"$wholePart $numerator/$denominator"
    }
  }

  private def getFraction(fraction: Fraction): Fraction = {
    val currentFractionNumerator = getCorrectNumerator(this)
    val additionalFractionNumerator = getCorrectNumerator(fraction)
    
    new Fraction(
      0,
      additionalFractionNumerator * currentFractionNumerator,
      fraction.denominator * denominator
    )
  }

  private def getNOD(a: Int, b: Int): Int = {
    var x = a
    var y = b
    while (y != 0) {
      val temp = y
      y = x % y
      x = temp
    }
    Math.abs(x)
  }

  private def getCorrectNumerator(fraction: Fraction): Int = {
    var fractionNumerator = fraction.numerator
    if (fraction.wholePart > 0) {
      fractionNumerator += fraction.denominator * fraction.wholePart
    }
    if (fraction.wholePart < 0) {
      fractionNumerator += fraction.denominator * (-fraction.wholePart)
      fractionNumerator *= -1
    }
    fractionNumerator
  }
}

object Fraction {
  def of(wholePart: Int, numerator: Int, denominator: Int): Fraction = {
    if (isValid(wholePart, numerator, denominator)) {
      new Fraction(wholePart, numerator, denominator)
    } else {
      throw new IllegalArgumentException("Введены некорректные данные")
    }
  }

  def isValid(wholePart: Int, numerator: Int, denominator: Int): Boolean = {
    val isCorrectNumerator = if (wholePart != 0) numerator >= 0 else true
    val isValidNumerator = (Math.abs(numerator) < Math.abs(denominator)) && isCorrectNumerator
    val isNonZeroDenominator = denominator > 0
    isValidNumerator && isNonZeroDenominator
  }

  def readFromTextFile(filename: String): Fraction = {
    Using(Source.fromFile(filename)) { source =>
      val content = source.mkString.trim
      parseFromString(content)
    }.get
  }

  def readFromBinaryFile(filename: String): Fraction = {
    Using(new DataInputStream(new FileInputStream(filename))) { dis =>
      Fraction.of(dis.readInt(), dis.readInt(), dis.readInt())
    }.get
  }

  private def parseFromString(str: String): Fraction = {
    if (str.contains("/")) {
      if (str.contains(" ")) {
        val parts = str.split(" ")
        val wholePart = parts(0).toInt
        val fractionParts = parts(1).split("/")
        Fraction.of(wholePart, fractionParts(0).toInt, fractionParts(1).toInt)
      } else {
        val fractionParts = str.split("/")
        Fraction.of(0, fractionParts(0).toInt, fractionParts(1).toInt)
      }
    } else {
      Fraction.of(str.toInt, 0, 1)
    }
  }

  // Сравнение дробей (можно переопределить для разных критериев)
  def compare(f1: Fraction, f2: Fraction): Boolean = {
    val a = f1.wholePart.toDouble + f1.numerator.toDouble / f1.denominator.toDouble
    val b = f2.wholePart.toDouble + f2.numerator.toDouble / f2.denominator.toDouble
    a < b
  }

  // Сортировка списка дробей (функциональный стиль)
  def sortFractionsFunctional(fractions: List[Fraction]): List[Fraction] = {
    FunctionalMergeSort.sortWithComparator(fractions)(compare)
  }

  // Сортировка массива дробей (императивный стиль)
  def sortFractionsImperative(fractions: Array[Fraction]): Unit = {
    ImperativeMergeSort.sortWithExternalComparator(fractions, compare)
  }
}

object FunctionalMergeSort {
  // Обобщенная сортировка слиянием (функциональный стиль)
  def mergeSort[T](list: List[T])(implicit ord: Ordering[T]): List[T] = {
    // Базовый случай
    if (list.length <= 1) list
    else {
      // Рекурсивное разделение
      val (left, right) = list.splitAt(list.length / 2)
      // Рекурсивное слияние
      merge(mergeSort(left), mergeSort(right))
    }
  }

  // Рекурсивное слияние
  private def merge[T](left: List[T], right: List[T])(implicit ord: Ordering[T]): List[T] = 
    (left, right) match {
      case (Nil, _) => right
      case (_, Nil) => left
      case (l :: ls, r :: rs) =>
        if (ord.lteq(l, r)) l :: merge(ls, right)
        else r :: merge(left, rs)
    }

  // Лексикографическая сортировка строк
  def lexicographicSort(strings: List[String]): List[String] = 
    mergeSort(strings)(Ordering.String)

  // Сортировка с пользовательской функцией сравнения
  def sortWithComparator[T](list: List[T])(compare: (T, T) => Boolean): List[T] = {
    implicit val customOrdering: Ordering[T] = Ordering.fromLessThan(compare)
    mergeSort(list)
  }
}

object ImperativeMergeSort {
  // Обобщенная сортировка слиянием (императивный стиль)
  def mergeSort[T](arr: Array[T])(implicit ord: Ordering[T]): Unit = {
    if (arr.length <= 1) return
    
    // Рекурсивное разделение
    val mid = arr.length / 2
    val left = arr.slice(0, mid)
    val right = arr.slice(mid, arr.length)
    
    mergeSort(left)
    mergeSort(right)
    
    // Циклическое слияние
    var (i, j, k) = (0, 0, 0)
    while (i < left.length && j < right.length) {
      if (ord.lteq(left(i), right(j))) {
        arr(k) = left(i)
        i += 1
      } else {
        arr(k) = right(j)
        j += 1
      }
      k += 1
    }
    
    // Однократное слияние оставшихся элементов
    while (i < left.length) {
      arr(k) = left(i)
      i += 1
      k += 1
    }
    
    while (j < right.length) {
      arr(k) = right(j)
      j += 1
      k += 1
    }
  }

  // Сортировка с внешним компаратором
  def sortWithExternalComparator[T](arr: Array[T], compare: (T, T) => Boolean): Unit = {
    implicit val customOrdering: Ordering[T] = Ordering.fromLessThan(compare)
    mergeSort(arr)
  }
}


object SortingExample extends App {
  val fractions = List(
    Fraction.of(1, 1, 2),
    Fraction.of(0, 3, 4),
    Fraction.of(2, 0, 1),
    Fraction.of(1, 2, 3)
  )

  // Функциональная сортировка
  val sortedFunctional = Fraction.sortFractionsFunctional(fractions)
  println("Функциональная сортировка:")
  sortedFunctional.foreach(println)

  // Императивная сортировка
  val array = fractions.toArray
  Fraction.sortFractionsImperative(array)
  println("\nИмперативная сортировка:")
  array.foreach(println)
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: scala/collection/immutable/List#splitAt().