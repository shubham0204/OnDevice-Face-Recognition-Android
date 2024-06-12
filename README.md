# On-Device Face Recognition In Android 

> A simple Android app that performs on-device face recognition by comparing FaceNet embeddings against a vector database of user-given faces


## Goals

* Produce on-device face embeddings with FaceNet and use them to perform face recognition on a user-given set of images
* Use modern Android development practices and recommended architecture guidelines while maintaining code simplicity and modularity

## Tools

1. [TensorFlow Lite](https://ai.google.dev/edge/lite) as a runtime to execute the FaceNet model
2. [MLKit Face Detector](https://developers.google.com/ml-kit/vision/face-detection/android) to crop faces from the image
3. [ObjectBox](https://objectbox.io) for on-device vector-store and NoSQL database

## Discussion

### How does the face-recognition pipeline work?

We use the FaceNet model, which given a 160 * 160 cropped face image, produces an embedding of 128 or 512 elements capturing facial features that uniquely identify the face. 

1. When users select an image, the app uses MLKit's `FaceDetector` to crop faces $X_1, X_2, ... , X_n$ from the image.