error id: `<none>`.
file:///C:/labs/MAGISTER/1%20course/TRPO/Lab-2-1/lab1/src/main/scala/my/scalafx/Fraction.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 144
uri: file:///C:/labs/MAGISTER/1%20course/TRPO/Lab-2-1/lab1/src/main/scala/my/scalafx/Fraction.scala
text:
```scala
import java.io.{DataOutputStream, IOException}
import java.nio.file.{Files, Path}

class Fraction private (private var _wholePart: Int, private @@var _numerator: Int, private var _denominator: Int) {

  // Getters
  def wholePart: Int = _wholePart
  def numerator: Int = _numerator
  def denominator: Int = _denominator

  // Setters with validation
  def wholePart_=(value: Int): Unit = {
    if (!Fraction.isValid(value, numerator, denominator)) {
      throw new IllegalArgumentException("Предоставлен не корректные данные")
    }
    _wholePart = value
  }

  def numerator_=(value: Int): Unit = {
    if (!Fraction.isValid(wholePart, value, denominator)) {
      throw new IllegalArgumentException(
        "Предоставлен не корректный числитель который больше либо равен знаменателю")
    }
    _numerator = value
  }

  def denominator_=(value: Int): Unit = {
    if (!Fraction.isValid(wholePart, numerator, value)) {
      throw new IllegalArgumentException(
        "Предоставлен не корректный знаменатель который меньше либо равен числителю")
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
  def writeToFileChar(): Unit = {
    val path = Path.of(System.getProperty("user.dir"), "/char.txt")
    Files.deleteIfExists(path)
    Files.createFile(path)
    val bf = Files.newBufferedWriter(path)
    try {
      bf.write(this.toString)
    } finally {
      bf.close()
    }
  }

  @throws[IOException]
  def writeToFileBinary(): Unit = {
    val path = Path.of(System.getProperty("user.dir"), "/byte.txt")
    Files.deleteIfExists(path)
    Files.createFile(path)
    val dos = new DataOutputStream(Files.newOutputStream(path))
    try {
      dos.write(wholePart)
      dos.write(numerator)
      dos.write(denominator)
    } finally {
      dos.close()
    }
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
    
    val tmpFraction = new Fraction(0, additionalFractionNumerator, fraction.denominator)
    val currentFraction = new Fraction(0, currentFractionNumerator, denominator)
    
    new Fraction(
      0,
      tmpFraction.numerator * currentFraction.numerator,
      tmpFraction.denominator * currentFraction.denominator
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
      throw new IllegalArgumentException("Введены не корректные данные")
    }
  }

  def isValid(wholePart: Int, numerator: Int, denominator: Int): Boolean = {
    val isCorrectNumerator = if (wholePart != 0) numerator >= 0 else true
    val isValidNumerator = (Math.abs(numerator) < Math.abs(denominator)) && isCorrectNumerator
    val isNonZeroDenominator = denominator > 0
    isValidNumerator && isNonZeroDenominator
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.