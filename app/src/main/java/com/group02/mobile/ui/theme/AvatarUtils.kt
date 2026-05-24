package com.group02.mobile.ui.theme

const val DEFAULT_AVATAR = "🦊"

/**
 * Returns a safe emoji avatar string.
 * If the stored photoUrl is a URL (http/content) or blank, falls back to DEFAULT_AVATAR.
 * This handles legacy data where a Google profile photo URL may have been stored.
 */
fun safeAvatar(photoUrl: String?): String {
    if (photoUrl.isNullOrBlank()) return DEFAULT_AVATAR
    if (photoUrl.startsWith("http") || photoUrl.startsWith("content://")) return DEFAULT_AVATAR
    return photoUrl
}
