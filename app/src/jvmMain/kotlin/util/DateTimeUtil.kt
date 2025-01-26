package util

import java.time.format.DateTimeFormatter
import java.util.Locale

val NO_DELIMITER_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.JAPAN)