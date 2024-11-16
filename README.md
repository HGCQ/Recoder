# 🛤️ 공유 앨범 앱 Recoder
> [Android_Project](https://github.com/HGCQ/AndroidProjects)에서 진행한 Recoder의 기존 기능을 보완하고 새로운 기능을 추가한 프로젝트입니다.   

## 목차

- [🛤️ 공유 앨범 앱 Recoder](#️-공유-앨범-앱-recoder)
  - [📋 프로젝트 소개](#-프로젝트-소개)
  - [👨‍👨‍👧‍👦 팀원 구성](#-팀원-구성)
  - [📃 개발 환경 및 개발 기간](#-개발-환경-및-개발-기간)
  - [🧩 역할 분담](#-역할-분담)
  - [💡 기능 목록](#-기능-목록)
  - [프로그램 설계](#프로그램-설계)
  - [🔧 페이지별 기능](#-페이지별-기능)
  - [프로젝트 후기](#프로젝트-후기)

<br>

## 📋 프로젝트 소개

- ✈️ 친구, 연인 또는 가족끼리 추억을 회상하는 추억 공유 앨범 앱입니다.
- 💑 특정 사람들과 어떤 추억이 있었는지 사진을 통해 회상할 수 있습니다.
- 📷 앨범마다 친구와 사진을 쉽게 공유할 수 있습니다.

<br>

![video](https://private-user-images.githubusercontent.com/115599902/386812165-dd9a2e5c-f5c7-4bc9-be0e-e68caa3c4073.gif?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MzE3MjM2NDUsIm5iZiI6MTczMTcyMzM0NSwicGF0aCI6Ii8xMTU1OTk5MDIvMzg2ODEyMTY1LWRkOWEyZTVjLWY1YzctNGJjOS1iZTBlLWU2OGNhYTNjNDA3My5naWY_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQxMTE2JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MTExNlQwMjE1NDVaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0wZmQzOGI5ZjExYjNhZDkxNTE5NzVmNTk0ZjQwZWZjNjhlOGM1NDg5YzkwZDQ3OGM2NzlmYmIzZDU4MDY4NTJjJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.C2gkxv9U0dSKY0inAWTOW4l7vEIrCXd481jNCkjAgvE)

<br>

## 👨‍👨‍👧‍👦 팀원 구성

| 이름 | Github |
| --- | --- |
| 👦 김명준(팀장) | [Mangjun](https://github.com/Mangjun) |
| 👦 김남규 | [namgue](https://github.com/namgue) |
| 👦 이동현 | [LEEDDONG](https://github.com/LEEDDONG) |
| 👦 전경섭 | [JKS8520](https://github.com/JKS8520) |
| 👧 조서윤 | [ewbrdjf](https://github.com/ewbrdjf) |

<br>

## 📃 개발 환경 및 개발 기간

### 🛠️ 개발 환경

- 클라이언트 : Android Studio(Iguana)
- 서버 : Spring Boot(3.3.3)
- 데이터베이스 : MySQL(8.0.32), Redis
- 소스 및 버전 관리 : Git, Github
- 배포 : AWS(EC2, S3), Docker

### 📅 개발 기간

- 전체 개발 기간 : 2024.08.19 ~ 2024.10.31
- UI 구현 : 2024.08.19 ~ 2024.10.31
- 기능 구현 : 2024.08.31 ~ 2024.10.24
- 테스트 : 2024.10.24 ~ 2024.10.31

### 🖊️ 브랜치 전략

- **main** : 배포할 때만 사용하는 브랜치입니다.
- **develop** : 개발 단계에서 main 역할을 하는 브랜치입니다.
- **feature** : 하나의 기능을 개발하는 브랜치입니다.

<br>

## 🧩 역할 분담

| 이름 | 역할 |
| :---: | --- |
| 👨‍🎓<br>김명준 | 프로그램 설계, 데이터베이스 설계 및 구축, 서버 Rest API 작성, 서버 배포, <br>서버 및 클라이언트 비즈니스 로직 구현, 클라이언트 화면 구현(로그인, 회원가입, 채팅, 친구 목록, 회원 검색, 좋아요), <br>README 작성, 간트 차트 작성, 발표 |
| 👨‍🎓<br>김남규 | 클라이언트 Rest API와 통신할 API 작성, 클라이언트 화면 구현(갤러리, 사진, 사진 휴지통, 정보 수정), QA |
| 👨‍🎓<br>이동현 | 클라이언트 내부 데이터베이스 구축, 클라이언트 화면 구현(그룹 목록, 그룹 생성, 그룹 설정, 앨범 목록, 앨범 생성, 앨범 휴지통), QA |
| 👨‍🎓<br>전경섭 | 개인과 공유 상호 이동 원활히 하는 기능, 애플리케이션 흐름도 작성, 발표, QA |
| 👩‍🎓<br>조서윤 | 디자인 작성, UI 설계 및 작성, 클라이언트 화면 구현(로딩 화면, 선택 화면, 나의 정보), 발표 자료 제작, README 작성, QA |

<br>

## 💡 기능 목록 

* 회원 기능
    * 회원 가입
    * 로그인 및 로그아웃
    * 회원 정보 수정
    * 회원 탈퇴
    * 팔로우 / 팔로잉
    * 검색 허용 및 차단
    * 프로필 사진 추가

* 그룹 기능
    * 그룹 생성
    * 그룹 삭제
    * 그룹 나가기
    * 그룹 대표 이미지 추가
    * 관리자 권한 부여 및 박탈
    * 회원 초대 및 추방

* 앨범 기능
    * 앨범 생성
    * 앨범 삭제
    * 휴지통 및 복원
    * 실시간 채팅

* 사진 기능
    * 사진 업로드
    * 사진 삭제
    * 휴지통 및 복원
    * 앨범 이동
    * 날짜로 검색
    * 지역별로 자동 분류

* 기존과 변경된 점
    * 친구 -> 팔로잉 / 팔로워 형식으로 수정
    * 회원들을 묶는 그룹을 추가
    * 실시간 채팅 추가
    * 앨범 및 사진에 휴지통 기능 추가
    * 사진의 날짜를 보이도록 하고, 시간 순서대로 볼 수 있도록 수정
    * 자신만의 포토북을 사용할 수 있도록 개인 페이지 추가
    * 사진의 위치 정보를 토대로 같은 지역끼리 자동 분류하는 기능 추가
    * 사진을 다른 앨범으로 이동 기능 추가
    * 사진 저장 위치를 서버 외부 디렉터리에 저장에서 S3 스토리지로 수정
    * 회원이 검색으로만 보여지도록 하는 기능 추가
    * 사진의 공유자가 누구인지 보여지도록 수정
    * 사진을 날짜로 검색하는 기능 추가

<br>

## 프로그램 설계

- 엔티티 분석
![Entity]()

<br>

-  테이블 설계
![Table]()

<br>

- 애플리케이션 흐름도
![Flow]()

<br>

## 🔧 페이지별 기능

### 로딩
![loading]()
>

<br>

### 선택
![select]()
>

<br>

### 로그인
<img src="https://github.com/HGCQ/Recoder/blob/main/data/login.jpg" alt="login" width="300" height="500"/>

>

<br>

### 회원가입
<img src="https://github.com/HGCQ/Recoder/blob/main/data/join.jpg" alt="join" width="300" height="500"/>

>

<br>

### 그룹 목록
<img src="https://github.com/HGCQ/Recoder/blob/main/data/grouplist.jpg" alt="grouplist" width="300" height="500"/>

>

<br>

### 그룹 생성
<img src="https://github.com/HGCQ/Recoder/blob/main/data/groupcreate.jpg" alt="groupcreate" width="300" height="500"/>

> 

<br>

### 그룹 설정
<img src="https://github.com/HGCQ/Recoder/blob/main/data/groupsetting.jpg" alt="groupsetting" width="300" height="500"/>

> 

<br>

### 앨범 목록
<img src="https://github.com/HGCQ/Recoder/blob/main/data/albumlist.jpg" alt="albumlist" width="300" height="500"/>

> 

<br>

### 앨범 생성
<img src="https://github.com/HGCQ/Recoder/blob/main/data/albumcreate.jpg" alt="albumcreate" width="300" height="500"/>

> 

<br>

### 앨범 휴지통
<img src="https://github.com/HGCQ/Recoder/blob/main/data/albumtrash.jpg" alt="albumtrash" width="300" height="500"/>

> 

<br>

### 갤러리
<img src="https://github.com/HGCQ/Recoder/blob/main/data/gallery.jpg" alt="gallery" width="300" height="500"/>

> 

<br>

### 사진 휴지통
<img src="https://github.com/HGCQ/Recoder/blob/main/data/phototrash1.jpg" alt="phototrash1" width="300" height="500"/>
<img src="https://github.com/HGCQ/Recoder/blob/main/data/phototrash2.jpg" alt="phototrash2" width="300" height="500"/>

> 

<br>

### 채팅
<img src="https://github.com/HGCQ/Recoder/blob/main/data/chat.jpg" alt="chat" width="300" height="500"/>

> 

<br>

### 사진
<img src="https://github.com/HGCQ/Recoder/blob/main/data/photo.jpg" alt="photo" width="300" height="500"/>

> 

<br>

### 팔로우 목록
<img src="https://github.com/HGCQ/Recoder/blob/main/data/followlist.jpg" alt="followlist" width="300" height="500"/>

> 

<br>

### 회원 검색
<img src="https://github.com/HGCQ/Recoder/blob/main/data/followadd.jpg" alt="followadd" width="300" height="500"/>
<img src="https://github.com/HGCQ/Recoder/blob/main/data/searchfollow.jpg" alt="searchfollow" width="300" height="500"/>

> 

<br>

### 좋아요
<img src="https://github.com/HGCQ/Recoder/blob/main/data/like.jpg" alt="like" width="300" height="500"/>

> 

<br>

### 나의 정보
<img src="https://github.com/HGCQ/Recoder/blob/main/data/mypage.jpg" alt="mypage" width="300" height="500"/>
<img src="https://github.com/HGCQ/Recoder/blob/main/data/mypage2.jpg" alt="mypage2" width="300" height="500"/>

> 

<br>

### 정보 수정
![modify]()
> 

<br>

## 프로젝트 후기

### 👦 김명준
이번 프로젝트를 통해 팀장의 역할이 얼마나 중요한지 깊이 깨닫게 되었습니다. 팀장의 리더십과 방향 설정이 팀 전체의 협업과 결과물의 완성도에 큰 영향을 미친다는 것을 직접 경험했습니다. 또한 프로젝트를 진행하면서 나의 부족한 점들을 명확히 알게 되었고, 이를 개선해야겠다는 동기를 얻었습니다.   

특히 의사소통 능력과 시간 관리, 그리고 문제를 구조적으로 접근하는 역량이 중요하다는 것을 깨달았습니다. 이러한 점들을 보완하기 위해 앞으로는 더 많은 실전 경험과 학습을 통해 성장해 나갈 계획입니다. 이번 프로젝트는 부족함을 발견하고 개선할 기회를 준 소중한 경험이었습니다.

### 👦 김남규


### 👦 이동현


### 👦 전경섭


### 👧 조서윤
