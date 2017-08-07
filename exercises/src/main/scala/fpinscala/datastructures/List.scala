package fpinscala.datastructures

import scala.annotation.tailrec

sealed trait List[+A] // `List` data type, parameterized on a type, `A`
case object Nil extends List[Nothing] // A `List` data constructor representing the empty list
/* Another data constructor, representing nonempty lists. Note that `tail` is another `List[A]`,
which may be `Nil` or another `Cons`.
 */
case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List { // `List` companion object. Contains functions for creating and working with lists.
  def sum(ints: List[Int]): Int = ints match { // A function that uses pattern matching to add up a list of integers
    case Nil => 0 // The sum of the empty list is 0.
    case Cons(x,xs) => x + sum(xs) // The sum of a list starting with `x` is `x` plus the sum of the rest of the list.
  }

  def product(ds: List[Double]): Double = ds match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x,xs) => x * product(xs)
  }

  def apply[A](as: A*): List[A] = // Variadic function syntax
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))

  val x = List(1,2,3,4,5) match {
    case Cons(x, Cons(2, Cons(4, _))) => x
    case Nil => 42
    case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
    case Cons(h, t) => h + sum(t)
    case _ => 101
  }

  def append[A](a1: List[A], a2: List[A]): List[A] =
    a1 match {
      case Nil => a2
      case Cons(h,t) => Cons(h, append(t, a2))
    }

  def foldRight[A,B](as: List[A], z: B)(f: (A, B) => B): B = // Utility functions
    as match {
      case Nil => z
      case Cons(x, xs) => f(x, foldRight(xs, z)(f))
    }

  def sum2(ns: List[Int]) =
    foldRight(ns, 0)((x,y) => x + y)

  def product2(ns: List[Double]) =
    foldRight(ns, 1.0)(_ * _) // `_ * _` is more concise notation for `(x,y) => x * y`; see sidebar


  def tail[A](l: List[A]): List[A] = l match {
    case Nil => sys.error("ERRORRRR")
    case Cons(_,t) => t
  }

  def setHead[A](l: List[A], h: A): List[A] = l match {
    case Nil => sys.error("ERRORRRR")
    case Cons(_,t) => Cons(h,t)
  }

  def drop[A](l: List[A], n: Int): List[A] = {
    @tailrec
    def go(i: Int, l2: List[A]): List[A] =
      if (i == n) l2
      else l2 match {
        case Nil => Nil
        case Cons(_,xs) => go(i+1,xs)
      }
    go(0,l)
  }

  def dropWhile[A](l: List[A], f: A => Boolean): List[A] = {
    @tailrec
    def go(l2: List[A]): List[A] = l2 match {
      case Nil => Nil
      case Cons(x,xs) if f(x) => go(xs)
      case _ => l2
    }
    go(l)
  }

  def init[A](l: List[A]): List[A] = {
    l match {
      case Nil => sys.error("emtpy list")
      case Cons(_,Nil) => Nil
      case Cons(x,xs) => Cons(x,init(xs))
    }
  }

  def length[A](l: List[A]): Int = foldRight(l,0)((_, acc) => acc+1)

  @annotation.tailrec
  def foldLeft[A,B](l: List[A], z: B)(f: (B, A) => B): B = l match {
    case Nil => z
    case Cons(x,xs) => foldLeft(xs,f(z,x))(f)
  }

  def sum3(l: List[Int]): Int = foldLeft(l,0)(_ + _)
  def product3(l: List[Double]): Double = foldLeft(l,1.0)(_ * _)
  def length3[A](l: List[A]): Int = foldLeft(l,0)((acc,_) => acc+1)

  def reverse[A](l: List[A]): List[A] = foldLeft(l,List[A]())((acc,value) => Cons(value,acc))

  def foldRight2[A,B](l: List[A],z:B)(f: (A,B) => B ): B = foldLeft(reverse(l),z)((x,y) => f(y,x))

 def append2[A](a1: List[A], a2: List[A]): List[A] = foldRight(a1,a2)(Cons(_,_))
  def concat[A](ls: List[List[A]]): List[A] = foldRight(ls, Nil:List[A])((a,b) => append(a,b))
  def add1(ls: List[Int]): List[Int] = ls match {
    case Nil => Nil
    case Cons(x,xs) => Cons(x+1,add1(ls))
  }
  def add1_2(ls: List[Int]): List[Int] = foldRight(ls, Nil:List[Int])((a,b) => Cons(a+1,b))
  def listDoubleToString(ls: List[Double]): List[String] = ls match {
    case Nil => Nil
    case Cons(x,xs) => Cons(x.toString, listDoubleToString(xs))
  }
  def listDoubleToString2(ls: List[Double]): List[String] = foldRight(ls,Nil:List[String])((a,b) => Cons(a.toString,b))

  def map[A,B](l: List[A])(f: A => B): List[B] = l match {
    case Nil => Nil
    case Cons(x,xs) => Cons(f(x),map(xs)(f))
  }

  def map2[A,B](l: List[A])(f: A => B): List[B] = foldRight2(l,Nil:List[B])((a,b) => Cons(f(a),b))
  def filter[A](as: List[A])(f: A => Boolean): List[A] = as match {
    case Nil => Nil
    case Cons(x,xs) if f(x) => Cons(x,filter(xs)(f))
    case Cons(x,xs) => filter(xs)(f)
  }
  def filter2[A](as: List[A])(f: A => Boolean): List[A] = foldRight2(as,Nil:List[A])((a,b) =>
    if (f(a))
      Cons(a,b)
    else
      b
  )

  def flatMap[A,B](as: List[A])(f: A => List[B]): List[B] = concat(map(as)(f))

  def flatMapFoldRight[A,B](as: List[A])(f: A => List[B]): List[B] = foldRight2(as, Nil:List[B])((a,b) => append(f(a),b))

  def filterViaFlatMap[A](as: List[A])(f: A => Boolean): List[A] = flatMap(as)(a => if (f(a)) List(a) else Nil)

  def addTwoLists(l1: List[Int], l2: List[Int]): List[Int] = (l1,l2) match {
    case (Nil,_) => Nil
    case (_,Nil) => Nil
    case (Cons(h1,t1),Cons(h2,t2)) => Cons(h1+h2,addTwoLists(l1,l2))
  }

  def zipWith[A,B,C](a: List[A], b: List[B])(f: (A,B) => C): List[C] = (a,b) match {
    case (Nil,_) => Nil
    case (_,Nil) => Nil
    case (Cons(h1,t1),Cons(h2,t2)) => Cons(f(h1,h2),zipWith(t1,t2)(f))
  }

  def hasSubsequence[A](sup: List[A], sub: List[A]): Boolean = ???

}
