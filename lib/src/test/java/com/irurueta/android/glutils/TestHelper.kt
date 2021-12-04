package com.irurueta.android.glutils

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any, R> T.callPrivateFuncWithResult(name: String, vararg args: Any?): R? =
    callPrivateFunc(name, *args) as? R

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any, R> T.callPrivateStaticFuncWithResult(
    name: String,
    vararg args: Any?
): R? = callPrivateStaticFunc(name, *args) as? R

inline fun <reified T : Any> T.callPrivateFunc(name: String, vararg args: Any?): Any? =
    T::class.declaredMemberFunctions
        .firstOrNull { it.name == name }
        ?.apply { isAccessible = true }
        ?.call(this, *args)

inline fun <reified T : Any> T.callPrivateStaticFunc(name: String, vararg args: Any?): Any? =
    T::class.declaredFunctions
        .firstOrNull { it.name == name }
        ?.apply { isAccessible = true }
        ?.call(this, *args)

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any, R> T.getPrivateProperty(name: String): R? =
    T::class.memberProperties
        .firstOrNull { it.name == name }
        ?.apply { isAccessible = true }
        ?.get(this) as? R

inline fun <reified T : Any, R> T.setPrivateProperty(name: String, value: R?) {
    val property = T::class.memberProperties.find { it.name == name }
    if (property is KMutableProperty<*>) {
        property.isAccessible = true
        property.setter.call(this, value)
    } else {
        property?.isAccessible = true
        property?.javaField?.isAccessible = true
        property?.javaField?.set(this, value)
    }
}
