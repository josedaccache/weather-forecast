# Weather Forecast Application

A simple weather application using MVVM clean architecture, Dagger2, Retrofit2 and RxJava that retrieves weather forecast.
Version 1.0

## Getting Started

Clone the project from Git repo and apply the two steps below:
- Sync Gradle
- Rebuild Project

### Prerequisites

[Android Studio](https://developer.android.com/studio/)

### How to run/install the app

Click on Run button, you should be able to choose between a connected android device or a configured emulator.

### Unit test description

The unit test consists of the below 
- CurrentCityViewModelTest to test the  CurrentCityViewModel and CurrentCityUseCase api calls
- CityViewModelTest to test the  CityViewModel and CityUseCase api calls

### How to generate a coverage report

To get a coverage report, execute the below command in Terminal window of Android Studio:

```
./gradlew createDebugCoverageReport
```

The report will be generated at the following path:
app/build/outputs/reports/coverage/debug/

## Author

Jose Daccache
