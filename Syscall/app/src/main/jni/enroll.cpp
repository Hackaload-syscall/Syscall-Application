//
// Created by Hwancheol on 2017-10-31.
//
#include <jni.h>
#include "com_example_youngseok_syscall_CameraActivity.h"

#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <android/log.h>

using namespace cv;
using namespace std;

#define EYE_DETECTION_CODE_SUCCESS 1
#define EYE_DETECTION_CODE_FACE_ERROR 2
#define EYE_DETECTION_CODE_EYE_ERROR 3

float eye_radius;

extern "C" {
JNIEXPORT void JNICALL
Java_com_example_youngseok_syscall_CamersaActivity_ConvertRGBA(JNIEnv *env, jobject instance,
                                                              jlong matAddrInput,
                                                              jlong matAddrResult) {
    Mat &matInput = *(Mat *) matAddrInput;
    Mat &matResult = *(Mat *) matAddrResult;

    cvtColor(matInput, matResult, CV_RGB2RGBA);
}
JNIEXPORT jlong JNICALL
Java_com_example_youngseok_syscall_CameraActivity_loadCascade(JNIEnv *env, jclass type,
                                                                  jstring cascadeFileName_) {
    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName_, 0);

    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);
    if (((CascadeClassifier *) ret)->empty()) {
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 실패  %s", nativeFileNameString);
    } else
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 성공 %s", nativeFileNameString);


    env->ReleaseStringUTFChars(cascadeFileName_, nativeFileNameString);

    return ret;
}


float resize(Mat img_src, Mat &img_resize, int resize_width) {

    float scale = resize_width / (float) img_src.cols;
    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    } else {
        img_resize = img_src;
    }
    return scale;
}


JNIEXPORT jint JNICALL
Java_com_example_youngseok_syscall_CameraActivity_detect(JNIEnv *env, jclass type,
                                                             jlong cascadeClassifier_face,
                                                             jlong cascadeClassifier_eye,
                                                             jlong matAddrInput,
                                                             jlong matAddrResult) {
    Mat &img_input = *(Mat *) matAddrInput;
    Mat &img_result = *(Mat *) matAddrResult;

    img_result = img_input.clone();

    std::vector<Rect> faces;
    Mat img_gray;

    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);

    Mat img_resize;
    float resizeRatio = resize(img_gray, img_resize, 640);

    //-- Detect faces
    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale(img_resize, faces, 1.1, 2,
                                                                     0 | CASCADE_SCALE_IMAGE,
                                                                     Size(30, 30));


    __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",
                        (char *) "face %d found ", faces.size());
    int result = EYE_DETECTION_CODE_FACE_ERROR;
    if(faces.size() == 0) return result;
    for (int i = 0; i < faces.size(); i++) {
        double real_facesize_x = faces[i].x / resizeRatio;
        double real_facesize_y = faces[i].y / resizeRatio;
        double real_facesize_width = faces[i].width / resizeRatio;
        double real_facesize_height = faces[i].height / resizeRatio;

        Point center(real_facesize_x + real_facesize_width / 2,
                     real_facesize_y + real_facesize_height / 2);
        ellipse(img_result, center, Size(real_facesize_width / 2, real_facesize_height / 2), 0, 0,
                360,
                Scalar(255, 0, 255), 30, 8, 0);


        Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width, real_facesize_height);
        Mat faceROI = img_gray(face_area);
        std::vector<Rect> eyes;

        //-- In each face, detect eyes
        ((CascadeClassifier *) cascadeClassifier_eye)->detectMultiScale(faceROI, eyes, 1.1, 2,
                                                                        0 | CASCADE_SCALE_IMAGE,
                                                                        Size(30, 30));
        result = EYE_DETECTION_CODE_EYE_ERROR;
        if(eyes.size() == 0) return result;
        for (size_t j = 0; j < eyes.size(); j++) {
            Point eye_center(real_facesize_x + eyes[j].x + eyes[j].width / 2,
                             real_facesize_y + eyes[j].y + eyes[j].height / 2);
            int radius = cvRound((eyes[j].width + eyes[j].height) * 0.25);
            circle(img_result, eye_center, radius, Scalar(255, 0, 0), 30, 8, 0);
        }
        __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",
                            (char *) "eye %d found ", eyes.size());
        if(eyes.size() == 2) {
            float abs_y_value = eyes[0].y - eyes[1].y;
            if(abs_y_value < 0)
                abs_y_value *= -1;
            if(abs_y_value < 10) {
                eye_radius += ((eyes[0].width + eyes[0].height) * 0.25 + (eyes[1].width + eyes[1].height * 0.25)) / 2;
                result = EYE_DETECTION_CODE_SUCCESS;
                return result;
            }
        }
        return EYE_DETECTION_CODE_EYE_ERROR;
    }
}
JNIEXPORT jfloat JNICALL
Java_com_example_youngseok_syscall_CameraActivity_getEyeRadius() {
    return eye_radius;
}

}