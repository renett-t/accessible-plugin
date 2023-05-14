package com.renettt.accessible.concurrent

import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.Comparator

object Threads {

    @JvmField
    val MAIN = Thread.currentThread()
//    val UI = Swing

    @JvmField
    val DB = ThreadPoolExecutor(
        1, 1,
        1, TimeUnit.MINUTES,
        LinkedBlockingQueue(), CustomThreadFactory()
    )

    @JvmField
    val SCHEDULERS = ScheduledThreadPoolExecutor(2)


    private val EXECUTORS = PriorityExecutors(4)

    fun threadPool(priority: Priority, task: () -> Unit) {
        EXECUTORS.getExecutor(priority)
            .execute(task)
    }

    fun threadPool(priority: Priority, task: Runnable) {
        EXECUTORS.getExecutor(priority)
            .execute(task)
    }

    class PriorityExecutors(poolSize: Int) : Comparator<Runnable> {

        private val threadPoolExecutor: Executor = ThreadPoolExecutor(
            poolSize, poolSize,
            0, TimeUnit.SECONDS,
            PriorityBlockingQueue(INITIAL_POOL_CAPACITY, this),
            CustomThreadFactory()
        )
        private val executors: Array<PriorityExecutor> = Array(Priority.VALUES.size) { i ->
            PriorityExecutor(Priority.VALUES[i])
        }
        private val runnablePriorityMap: WeakHashMap<Runnable, Priority> = WeakHashMap()

        override fun compare(runnable1: Runnable, runnable2: Runnable): Int {
//                        synchronized(runnablePriorityMap) {
            val runnable1PriorityOrdinal: Int = runnablePriorityMap[runnable1]?.ordinal ?: 0
            val runnable2PriorityOrdinal: Int = runnablePriorityMap[runnable2]?.ordinal ?: 0
//            }
            return runnable1PriorityOrdinal - runnable2PriorityOrdinal
        }

        fun getExecutor(priority: Priority): Executor {
            return executors[priority.ordinal]
        }

        private inner class PriorityExecutor(private val priority: Priority) : Executor {
            override fun execute(runnable: Runnable) {
                synchronized(runnablePriorityMap) {
                    runnablePriorityMap[runnable] = priority
                }
                threadPoolExecutor.execute(runnable)
            }
        }

        companion object {
            private const val INITIAL_POOL_CAPACITY = 10
        }

    }

    class CustomThreadFactory(private val poolPriority: Priority = Priority.MEDIUM) : ThreadFactory {
        private val threadGroup: ThreadGroup
        private val threadNamePrefix: String

        init {
            val securityManager = System.getSecurityManager()
            threadGroup = if (securityManager != null)
                securityManager.threadGroup
            else
                Thread.currentThread().threadGroup as ThreadGroup

            threadNamePrefix = "pool-${poolNumber.getAndIncrement()}-thread-"
        }

        private val threadNumber = AtomicInteger(1)

        override fun newThread(runnable: Runnable): Thread {
            return Thread(
                threadGroup, runnable,
                threadNamePrefix + threadNumber.getAndIncrement(), 0
            ).apply {
                isDaemon = false
                priority = poolPriority()
            }
        }

        companion object {
            private val poolNumber = AtomicInteger(1)
        }
    }

    enum class Priority(val value: Int) {

        HIGHEST(Thread.MAX_PRIORITY - 1),  //10
        HIGH(Thread.NORM_PRIORITY + 2),
        MEDIUM(Thread.NORM_PRIORITY),  // 5
        LOW(Thread.MIN_PRIORITY + 2),
        LOWEST(Thread.MIN_PRIORITY); // 1

        companion object {
            val VALUES = values()
        }

        operator fun invoke(): Int = value
    }

}
