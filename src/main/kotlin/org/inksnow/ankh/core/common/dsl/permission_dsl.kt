package org.inksnow.ankh.core.common.dsl

import org.bukkit.permissions.Permissible

fun Permissible.anyPermission(vararg permissions: String): Boolean = permissions.any { hasPermission(it) }

fun Permissible.allPermission(vararg permissions: String): Boolean = permissions.all { hasPermission(it) }