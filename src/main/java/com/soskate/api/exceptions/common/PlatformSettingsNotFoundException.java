package com.soskate.api.exceptions.common;

public class PlatformSettingsNotFoundException extends ApiException {
    public PlatformSettingsNotFoundException(String message) {
        super(ErrorCode.PLATFORM_SETTINGS_NOT_FOUND, message);
    }
}
