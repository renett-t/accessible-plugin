package com.renettt.accessible.checks

class HashMapResultMetadata : ResultMetadata {
    private val map = hashMapOf<String, TypedValue>()
    override fun getStringMessage(): String {
        return "Ха"
    }

    override fun getString(key: String): String {
        return checkReturnValue(key, TypedValue.Type.STRING) as String
    }

    override fun getBoolean(key: String): Boolean {
        return checkReturnValue(key, TypedValue.Type.STRING) as Boolean
    }

    override fun getInt(key: String): Int {
        return checkReturnValue(key, TypedValue.Type.STRING) as Int
    }

    override fun getDouble(key: String): Double {
        return checkReturnValue(key, TypedValue.Type.DOUBLE) as Double
    }

    private fun checkReturnValue(key: String, typedValue: TypedValue.Type): Any? {
        val tv: TypedValue? = map[key]
        if (tv == null) {
            throw invalidKeyException(key)
        } else if (typedValue != tv.type) {
            throw invalidTypeException(key, typedValue, tv.type)
        }

        return tv.value
    }

    override fun putString(key: String, value: String) {
        map[key] = TypedValue(TypedValue.Type.STRING, value)
    }

    override fun putBoolean(key: String, value: Boolean) {
        map[key] = TypedValue(TypedValue.Type.BOOLEAN, value)
    }

    override fun putInt(key: String, value: Int) {
        map[key] = TypedValue(TypedValue.Type.INT, value)
    }

    override fun putDouble(key: String, value: Double) {
        map[key] = TypedValue(TypedValue.Type.DOUBLE, value)
    }

    private fun invalidKeyException(key: String): NoSuchElementException {
        return NoSuchElementException(
            "No HashMapResultMetadata element found for key '$key'."
        )
    }

    private fun invalidTypeException(
        key: String,
        requestedType: TypedValue.Type,
        foundType: TypedValue.Type
    ): ClassCastException {
        return ClassCastException(
            "Invalid type '"
                    + requestedType.name
                    + "' requested from HashMapResultMetadata for key '"
                    + key
                    + "'.  Found type '"
                    + foundType.name
                    + "' instead."
        )
    }
}


class TypedValue(var type: Type, var value: Any?) {
    /**
     * Поддерживаемые типы
     */
    enum class Type {
        BOOLEAN, INT, LONG, DOUBLE, STRING;
//
//        fun toProto(): TypeProto {
//            return Preconditions.checkNotNull(TYPE_MAP[this])
//        }
//
//        companion object {
//            fun fromProto(proto: TypeProto): Type {
//                return Preconditions.checkNotNull(TYPE_MAP.inverse()[proto])
//            }
//        }
    }

    fun <T> getValueAs(type: Type): T {
        try {
            val result = when (type) {
                Type.BOOLEAN -> value as? T
                Type.INT -> value as? T
                Type.LONG -> value as? T
                Type.DOUBLE -> value as? T
                Type.STRING -> value as? T
            } ?: throw ClassCastException()

            return result

        } catch (e: ClassCastException) {
            throw IllegalArgumentException("Cannot infer $type from value: $value. Initial type: ${type.name}", e)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is TypedValue) {
            return false
        }
        return if (type != other.type) {
            false
        } else value == other.value
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    // For debugging
    override fun toString(): String {
        return value.toString()
    }
}
