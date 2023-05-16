package com.renettt.accessible.utils.event


interface ImmutableObservableEvent<in Handler : Any> {
    operator fun plusAssign(handler: Handler)
    operator fun minusAssign(handler: Handler)
}

interface MutableObservableEvent<in Handler : Any, Sender : Any, Argument> : ImmutableObservableEvent<Handler> {
    operator fun invoke(sender: Sender, args: Argument)
}

abstract class ObservableEventBase<Handler : Any, Sender : Any, Argument> : MutableObservableEvent<Handler, Sender, Argument> {

    private val handlers = mutableMapOf<String, Handler>()
    private var lock = 0
    private var pendingActions: MutableList<PendingEditBase<Handler>>? = null

    private val editQueue: MutableList<PendingEditBase<Handler>>?
        get() {
            if (pendingActions == null) {
                pendingActions = ArrayList()
            }
            return pendingActions
        }

    private val Handler.key: String
        get() = "${this::class.java.name}_${this.hashCode()}"


    override operator fun plusAssign(handler: Handler) {
        synchronized(handlers) {
            if (lock > 0) {
                editQueue?.add(PendingAdding(handler))
            } else {
                handlers[handler.key] = handler
            }
        }
    }


    override operator fun minusAssign(handler: Handler) {
        synchronized(handlers) {
            if (lock > 0) {
                editQueue?.add(PendingRemoving(handler))
            } else {
                handlers.remove(handler.key)
            }
        }
    }

    override operator fun invoke(sender: Sender, args: Argument) {
        synchronized(handlers) {
            lock++
        }

        try {
            for (handler in handlers.values) {
                notifyHandler(handler, sender, args)
            }
        } finally {
            synchronized(handlers) {
                lock--
                if (lock == 0 && pendingActions != null) {
                    for (action in pendingActions!!)
                        action.run(handlers)
                    pendingActions = null
                }
            }
        }
    }

    protected abstract fun notifyHandler(handler: Handler, sender: Sender, args: Argument)

    private abstract class PendingEditBase<Handler : Any> constructor(protected val argument: Handler) {

        abstract fun run(collection: MutableMap<String, Handler>)
    }

    private inner class PendingAdding(argument: Handler) : PendingEditBase<Handler>(argument) {

        override fun run(collection: MutableMap<String, Handler>) {
            collection[argument.key] = argument
        }
    }

    private inner class PendingRemoving(argument: Handler) : PendingEditBase<Handler>(argument) {

        override fun run(collection: MutableMap<String, Handler>) {
            collection.remove(argument.key)
        }
    }
}

abstract class ObservableEvent<Handler : Any, Sender : Any, Argument>(private val sender: Sender) : ObservableEventBase<Handler, Sender, Argument>() {

    operator fun invoke(args: Argument) {
        super.invoke(sender, args)
    }
}
