## 👨‍💻 프로젝트 소개
<p align="center">
  <img src="https://github.com/onihcsari/promise_application/blob/master/app/%EC%95%BD%EC%86%8D%EC%96%B4%ED%94%8C_%EB%A1%9C%EA%B7%B8%EC%9D%B8%ED%99%94%EB%A9%B4.png" width="30%" height="30%">
  <img src="https://github.com/onihcsari/promise_application/blob/master/app/%EC%95%BD%EC%86%8D%EC%96%B4%ED%94%8C_%EC%95%BD%EC%86%8D%EC%83%81%EC%84%B8%ED%99%94%EB%A9%B4.png" width="30%" height="30%">
  <img src="https://github.com/onihcsari/promise_application/blob/master/app/%EC%95%BD%EC%86%8D%EC%96%B4%ED%94%8C_%EC%95%BD%EC%86%8D%EC%84%A4%EC%A0%95%ED%99%94%EB%A9%B4.png" width="30%" height="30%">
  <img src="https://github.com/onihcsari/promise_application/blob/master/app/%EC%95%BD%EC%86%8D%EC%96%B4%ED%94%8C_%EC%95%BD%EC%86%8D%EC%9C%84%EC%B9%98%ED%99%95%EC%9D%B8%ED%99%94%EB%A9%B4.png" width="30%" height="30%">
  <img src="https://github.com/onihcsari/promise_application/blob/master/app/%EC%95%BD%EC%86%8D%EC%96%B4%ED%94%8C_%EC%95%BD%EC%86%8D%ED%91%B8%EC%8B%9C%EC%95%8C%EB%A6%BC%ED%99%94%EB%A9%B4.png" width="30%" height="30%">
</p>

### 약속관리 어플리케이션

> 간편하게 약속을 설정하고 관리하는 데 도움을 주는 어플리케이션입니다. 약속을 설정한 후 당일 혹은 지정된 시간 전에 푸시 알림을 받게 되고 당일 일정 시간 전부터 약속한 인원과의 위치 공유를 통해 서로의 위치를 확인할 수 있습니다.

</br>

## 📌 주요 기능
#### ✔ 간단한 약속 설정
- 앱 내의 약속설정 페이지에서 약속에 관련된 여러 내용 기입
    - 날짜, 시간, 장소, 상세 내용, 인원 등
- 어플에서 제공하는 UID를 통해 약속 인원간의 동기화 가능
#### ✔ 캘린더 형식을 통한 약속 확인
- 캘린더 형식을 활용하여 월간, 주간으로 약속 확인 가능
- 날짜를 선택하면 그 날 설정한 약속의 상세 내용 확인 가능
#### ✔ 약속 당일 푸시 알림
- 약속이 가까워지면 알림을 받아 잊지 않고 준비 가능
- 설정된 시간에 맞춰서 자동으로 푸시 알림 발송
#### ✔ 실시간 위치공유 및 상대 위치 확인
- 약속 당일, 참가자들의 현재 위치를 지도에서 실시간으로 확인 가능
- 상대방의 예상 도착 시간을 확인하고, 이동 경로 파악 가능

</br>


## 📲 담당 기능
    
#### • 캘린더 뷰 설정
MaterialCalendarView를 사용하여 달력을 표시

    <기능>
    1. 캘린더뷰를 사용하여 기본 달력을 월별로 설정
    2. 날짜 선택시 해당 날짜의 이벤트를 Firebase에서 가져와 리스트로 표시

#### • 약속 정보 표시
detailActivity, deleteAppointment를 이용하여 구현

    <기능>
    1. 약속에 설정된 상세 내용들을 화면에 표시
    2. 사용자 UID, 날짜, 시간 등을 기준으로 약속을 찾아 삭제

#### • 이벤트 목록 표시
fetchEventsFromFirebase, showEvents를 이용하여 구현

    <기능>
    1. 특정 날짜 선택 시 Firebase Realtime Database에서 이벤트를 가져와 List에 표시

#### • 알림 설정
NotificationManagerCompat, NotificationChannel을 사용

    <기능>
    1. 앱 실행 시 알림 권한 확인 및 권한이 부여되지 않았을 시 사용자에게 요청
    2. 푸시 알림 표시를 위한 채널을 설정하고 사용자에게 알림을 전송

#### • 위치 권한 및 요청
ACCESS_FINE_LOCATION, FusedLocationProviderClient, LocationRequest를 이용하여 구현

    <기능>
    1. 앱이 실행될 때 위치 권한을 확인 및 권한이 부여되지 않았을 시 사용자에게 요청
    2. GPS를 통한 정확한 위치 정보 획득
    3. 위치 업데이트 및 정확도와 간격을 설정(정확도는 높게, 업데이트 간격은 10초로 설정)

#### • 위치 정보 저장 및 표시
Kakao Map API, MapView, loadUserLocations 사용

    <기능>
    1. 위치가 변경될 때마다 Firebase Database에 저장됨(위치 정보는 위도와 경도 형태로 저장)
    2. 다른 사용자의 위치를 Database에서 가져와 지도에 마커로 표시 및 실시간으로 감지 및 처리
    3. 목적지 좌표를 검색하고 지도에 마커로 표시

#### • Firebase 환경 구축 및 연동
Firebase의 Authentication, Realtime Database 사용

    <기능>
    1. 사용자 인증을 통해 상태 확인 및 사용자의 UID를 통한 관리
    2. Realtime Database를 통한 실시간 위치 추적 및 특정 데이터 조회

</br>
