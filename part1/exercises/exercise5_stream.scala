abstract class Stream[+A] {
    def headOption: Option[A] = this match {
      case Empty => None
      case Cons(h, _) => Some(h())
    }
  
    // exercise 5.1
    def toList: List[A] = this match {
      case Empty => List()
      case Cons(h, t) => h() :: t().toList
    }
  
    // exercise 5.2
    def take(n: Int): Stream[A] = this match {
      case Cons(h, t) if n > 1 => Stream.cons(h(), t().take(n - 1))
      case Cons(h, _) if n == 1 => Stream.cons(h(), Stream.empty)
      case _ => Stream.empty
    }
  
    def drop(n: Int): Stream[A] = this match {
      case Cons(_, t) if n > 0 => t().drop(n - 1)
      case _ => this
    }
  
    // exercise 5.3
    def takeWhile(p: A => Boolean): Stream[A] = this match {
      case Cons(h, t) if p(h()) => Stream.cons(h(), t().takeWhile(p))
      case _ => Stream.empty
    }
  
    def exists(p: A => Boolean): Boolean = this match {
      case Cons(h, t) => p(h()) || t().exists(p)
      case _ => false
    }
  
    def foldRight[B](z: => B)(f: (A, => B) => B): B = this match {
      case Cons(h, t) => f(h(), t().foldRight(z)(f))
      case _ => z
    }
  
    def existsByFold(p: A => Boolean): Boolean = {
      foldRight(false)((a, b) => p(a) || b)
    }
  
    // exercise 5.4
    def forAll(p: A => Boolean): Boolean = {
      foldRight(true)((a, b) => p(a) && b)
    }
  
    // exercise 5.5
    def takeWhileByFoldRight(p: A => Boolean): Stream[A] = {
      foldRight(Stream.empty[A])((a, b) => if (p(a)) Stream.cons(a, b) else Stream.empty)
    }
  
    // exercise 5.6 (hard)
    def headOptionByFoldRight: Option[A] = {
      foldRight(None: Option[A])((a, _) => Some(a))
    }
  
    // exercise 5.7
    def map[B](f: A => B): Stream[B] = {
      foldRight(Stream.empty: Stream[B])((a, b) => Stream.cons(f(a), b))
    }
  
    def filter(f: A => Boolean): Stream[A] = {
      foldRight(Stream.empty: Stream[A])((a, b) => if (f(a)) Stream.cons(a, b) else b)
    }
  
    def append[B >: A](s: Stream[B]): Stream[B] = {
      foldRight(s)((a, b) => Stream.cons(a, b))
    }
  
    def flatMap[B](f: A => Stream[B]): Stream[B] = {
      foldRight(Stream.empty: Stream[B])((a, b) => f(a).append(b))
    }
  }
  case object Empty extends Stream[Nothing]
  case class Cons[+A](h: () => A, t: () => Stream[A]) extends Stream[A]
  
  
  object Stream {
  
    def if2[A](cond: Boolean, onTrue: () => A, onFalse: () => A): A = {
      if (cond) onTrue() else onFalse()
    }
  
    def maybeTwice(b: Boolean, i: => Int): Int = if (b) i+i else 0
  
    def cons[A](hd: => A, tl: => Stream[A]): Stream[A] = {
      lazy val head = hd
      lazy val tail = tl
      Cons(() => head, () => tail)
    }
    def empty[A]: Stream[A] = Empty
  
    def apply[A](as: A*): Stream[A] = {
      if (as.isEmpty) empty
      else cons(as.head, apply(as.tail: _*))
    }
  
    val ones: Stream[Int] = Stream.cons(1, ones)
  
    // exercise 5.8
    def constant[A](a: A): Stream[A] = {
      lazy val tail: Stream[A] = Cons(() => a, () => tail)
      tail
    }
  
    // exercise 5.9
    def from(n: Int): Stream[Int] = {
      cons(n, from(n + 1))
    }
  
    // exercise 5.10
    def fibs(): Stream[Int] = {
      def loop(i1: Int, i2: Int): Stream[Int] = {
        cons(i1, loop(i2, i1 + i2))
      }
      loop(0, 1)
    }
  
    def main(args: Array[String]): Unit = {
      println(List(1, 2, 3, 4).map(_ + 10).filter(_ % 2 == 0).map(_ * 3))
  
      val a = 20
      if2(a < 18,
        () => println("Access is denied"),
        () => println("Access is allowed"))
  
      val x = maybeTwice(true, { println("hi"); 1+41 })
      println("x = " + x)
  
      val s = Stream(3, 2, 1)
      println(s.headOption)
  
      // test exercise 5.1
      println(s.toList)
  
      // test exercise 5.2
      println(s.take(2).toList)
      println(s.drop(2).toList)
  
      // test exercise 5.3
      val p = (i: Int) => i%3==0
      println(s.takeWhile(p).toList)
  
      val p1 = (i: Int) => i%2==0
      println("Is element % 2 == 0 in Stream: " + s.exists(p1))
  
      // test exercise 5.4
      println("Elements: " + s.toList)
      println("Is all el > 0: " + s.forAll(_ > 0))
      println("Is all el > 1: " + s.forAll(_ > 1))
  
      // test exercise 5.5
      val p2 = (i: Int) => i%3==0
      println(s.takeWhileByFoldRight(p2).toList)
  
      // test exercise 5.6
      println(s.headOption)
  
      // test exercise 5.7
      println(Stream(1,2,3,4).map(_ + 10).filter(_ % 2 == 0).append(Stream(33)).toList)
      println(12 :: Stream(3,4).map(_ + 10).filter(_ % 2 == 0).toList)
  
      println(ones.take(5).toList)
      println(ones.take(5).map(_ + 1).toList)
  
      println(from(5).headOption)
    }
  }