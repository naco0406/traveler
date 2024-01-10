# Mr. 길버트
![대지 1_4](https://github.com/naco0406/traveler/assets/129248939/7b8a55ba-b38d-4c19-8a7d-04fd58aa6d24)


## 💻 프로젝트 개요

인터넷 속에 흩어진 정보들을 일일히 조합하여 여행 계획을 세우셨나요? 그런 불편함을 해결해줄 앱을 소개합니다!

- 본인만의 여행 경로 만들기
- 다른 사람들의 여행 경로 공유 게시판

## 👫 팀원

양혜민 - DGIST 컴퓨터공학과 20학번

고영 - 카이스트 전산학부 21학번

## 개발환경

- IDE : Android Studio
- Front : Kotlin
- Server : Flask
- Database : mongoDB
- SDK : Naver

---

## 주요 기능 소개

### Main 화면
<p align="center", width="100%">
  <img width="30%" height="30%" alt="자산 17@2x" src="https://github.com/naco0406/traveler/assets/129248939/68910b05-ed7b-4be2-acee-79730f33f516">

</p>
- 현재 인기 있는 여행지(도시, 장소)들을 한눈에 살펴볼 수 있습니다.
    - 관심있는 여행지를 선택하면 여행 경로 추천 게시글 탭으로 이동합니다.
- 하단에 나의 여행 정보를 담고 있는 탭이 있으며, 위로 스크롤해서 올리면 어떤 여행 경로를 계획하고 있는지 살펴볼 수 있습니다.

### 로그인 & 회원가입 기능

<img width="30%" height="30%" alt="자산 15@2x" src="https://github.com/naco0406/traveler/assets/129248939/55b05b71-01e4-4767-a7ab-0070acda2b44">

<로그인>
- 자체 로그인, 네이버 로그인을 할 수 있는 기능을 구현하였습니다.
- 로그인 정보가 일치하지 않으면 로그인 실패 창이 뜨며 사용자에게 해당 사실을 알립니다.
- 회원 가입 텍스트를 클릭하면 회원가입 창으로 넘어갑니다.
- 로그인에 성공할 경우, 자동적으로 마이페이지로 넘어갑니다. 또한 하단에 나만의 여행 경로를 설정할 수 있는 탭이 추가됩니다.

<회원가입>

- 자체 회원가입, 네이버 로그인을 통한 회원가입 기능을 구현하였습니다.
- 회원가입을 완료하면 회원가입 성공 알림창이 뜹니다.

### 마이페이지
<img width="30%" height="30%" alt="자산 14@2x" src="https://github.com/naco0406/traveler/assets/129248939/d0391a93-a2fc-4077-9b64-35e47ccabe30">

- 사용자의 이름, 닉네임, 핸드폰 정보를 확인할 수 있습니다.
- 각 텍스트를 길게 누르면 editText가 생성되면서 사용자가 수정할 수 있고, 옆에 수정 완료 버튼을 누르면 수정된 내용으로 업데이트 됩니다.
- 로그아웃 버튼을 누르면 다시 로그인 페이지로 돌아가게 됩니다.

### 여행 경로 추천 게시판
<p align="center", width="100%">
  <img width="30%" height="30%" alt="자산 16@2x" src="https://github.com/naco0406/traveler/assets/129248939/4211532e-cc76-474c-a42c-46ec043ade66">
  <img width="30%" height="30%" alt="자산 12@2x" src="https://github.com/naco0406/traveler/assets/129248939/5320a8d3-2e56-400f-95e9-83456eb1e93b">

</p>
- 사용자들이 실제로 경험해본, 자신만의 여행 경로들이 게시판에 추천순대로 보여집니다.
- 상단의 검색창에 도시 이름이나 여행지의 태그에 해당되는 키워드를 입력하면 그에 맞게 필터링된 글들만 보이게 됩니다.
- 오른쪽 상단에 필터 버튼을 누르면 내가 계획하는 여행 인원, 기간을 입력할 수 있고, 그에 맞는 게시물들이 보여집니다.
- 새로 고침 버튼을 누르면 필터 기능이 사라지고, 모든 게시물들이 추천순대로 보여집니다.
  
<img width="30%" height="30%" alt="자산 11@2x" src="https://github.com/naco0406/traveler/assets/129248939/8b2ff7d7-d53a-4a48-92c2-9ac4fd1c46cd">

- 사용자가 관심있는 여행경로 게시물을 선택하면, 추천 경로에 대한 자세한 정보가 담긴 페이지로 이동합니다.
    - 해당 페이지에서 여행 경로가 마음에 들 경우, 하단 버튼을 눌러 나만의 여행 경로에 추가해줍니다.

### 나만의 여행 경로 설정하기
<p align="center", width="100%">
  <img width="30%" height="30%" alt="자산 13@2x" src="https://github.com/naco0406/traveler/assets/129248939/abb52038-27f0-4e3e-97da-a9da76ecb7a8">
  <img width="30%" height="30%" alt="자산 10@2x" src="https://github.com/naco0406/traveler/assets/129248939/3f928236-6ad1-4b45-b499-e7fbe16b5e1b">
</p>

- 추천 여행 경로 중에서 import를 통해 나만의 여행 경로에 쉽게 추가하거나, 장소 추가 버튼을 통해 직접 장소를 검색하여 추가할 수 있습니다.
- 내가 추가한 여행지가 상단의 지도에 표시되어 경로의 효율성을 한눈에 살펴볼 수 있습니다.
- 저장을 누르면 나만의 여행 경로 설정이 완료되며, 데이터 베이스에 저장됩니다.

[APK 다운로드](https://drive.google.com/file/d/1y2uudgB_FZTviuNIXBMxhaGnlkO2WY5V/view?usp=sharing)
