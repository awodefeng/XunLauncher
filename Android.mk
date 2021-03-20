#
# Copyright (C) 2013 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

SRC_ROOT := src/com/xxun/xunlauncher

LOCAL_PACKAGE_NAME := XunLauncher

LOCAL_OVERRIDES_PACKAGES := Home Launcher2 Launcher3 ClassicHome

LOCAL_CERTIFICATE := platform

#LOCAL_STATIC_JAVA_LIBRARIES := \
#    android-support-v4 \
#    xungsontwo \
#    qrcode \
#    uploadfile

LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \
    xungsontwo

LOCAL_JAVA_LIBRARIES := framework framework2
LOCAL_JAVA_LIBRARIES += ims-common

LOCAL_SDK_VERSION := current

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res

LOCAL_SRC_FILES := $(call all-java-files-under, src) \
	src/com/xiaoxun/xiaoxuninstallapk/IProgressCallBack.aidl \
	src/com/xiaoxun/xiaoxuninstallapk/IAppStoreService.aidl \
	src/com/xxun/xunlauncher/xunaidl/IxunLauncher.aidl
include $(BUILD_PACKAGE)

# X: add by jxring for gson 2017.10.20 start
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := xungsontwo:libs/gson-2.2.4.jar
include $(BUILD_MULTI_PREBUILT)
# X: add by jxring for gson 2017.10.20 end

#include $(CLEAR_VARS)
#LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := uploadfile:libs/uploadfile.jar
#include $(BUILD_MULTI_PREBUILT)

#include $(CLEAR_VARS)
#LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := qrcode:libs/xun_zxing.jar
#include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))




