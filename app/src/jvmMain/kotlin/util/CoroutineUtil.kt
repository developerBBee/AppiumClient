package util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val IOScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
