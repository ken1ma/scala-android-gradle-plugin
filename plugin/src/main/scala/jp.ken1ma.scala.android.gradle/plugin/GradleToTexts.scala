package jp.ken1ma.scala.android.gradle
package plugin

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._
import java.nio.file.Path
import java.io.File
import java.lang.{Iterable => JavaIterable}

object GradleToTexts {
  // FileTree is FileCollection, and FileCollection is Iterable<File>
  implicit class JavaIterableFileTextOps(val files: JavaIterable[File]) extends AnyVal {
    def toText: String = stripCommonPrefix(files.asScala.toSeq.map(_.toPath.toAbsolutePath)).sorted.mkString(", ")
  }

  def stripCommonPrefix(files: Seq[Path]): Seq[Path] = { // TODO: can Seq be generalized?
    computeCommonPrefix(files) match {
      case Some(commonPrefix) => files.map(commonPrefix.relativize)
      case None => files
    }
  }

  def computeCommonPrefix(files: Iterable[Path]): Option[Path] = files.headOption.map { head =>
    files.tail.fold(head) { (path1, path2) =>
      @tailrec def loop(remainings: Iterator[(Path, Path)], commonLen: Int): Path = {
        def takeUntilCommonLen = {
          // Path.subpath returns a relative path
          // and Path.toAbsolutePath typically resolve against the file system default directory
          @tailrec def stripLast(path: Path, count: Int): Path = {
            if (count > 0) stripLast(path.getParent, count - 1)
            else           path
          }
          stripLast(path1, path1.getNameCount - commonLen)
        }
        if (remainings.hasNext) {
          val (elem1, elem2) = remainings.next()
          if (elem1 == elem2) loop(remainings, commonLen + 1)
          else                takeUntilCommonLen

        } else
          takeUntilCommonLen
      }
      loop(path1.iterator.asScala.zip(path2.iterator.asScala), 0)
    }
  }
}
