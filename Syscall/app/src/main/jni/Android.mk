LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

#opencv library
OPENCVROOT:= /Users/youngseok/Desktop/Hyundai/OpenCV-android-sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}/sdk/native/jni/OpenCV.mk


LOCAL_MODULE    := native-lib
LOCAL_SRC_FILES := enroll.cpp
LOCAL_LDLIBS += -llog -landroid

include $(BUILD_SHARED_LIBRARY)
