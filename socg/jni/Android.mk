LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := gmeplayer
LOCAL_SRC_FILES := $(wildcard gme/*.cpp)
LOCAL_SRC_FILES += $(wildcard *.cpp)
LOCAL_LDLIBS := -lz
include $(BUILD_SHARED_LIBRARY)