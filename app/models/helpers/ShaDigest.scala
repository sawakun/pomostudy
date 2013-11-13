package models.helpers
import java.security.MessageDigest

class ShaDigest(str: String) {
  val digestString: String = {
    val md = MessageDigest.getInstance("SHA-256")
    val salt = "darkknight"
      md.update((str+salt).getBytes)
      md.digest.foldLeft("") { (s, b) => s + "%02x".format(if(b < 0) b + 256 else b) }
  }
}

