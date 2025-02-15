package util

import java.time.format.DateTimeFormatter

val DATE_TIME_SEPARATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")

val NO_DELIMITER_MILLIS_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
