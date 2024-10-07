# 🛤️ 공유 앨범 앱 Recoder
> [Android_Project](https://github.com/HGCQ/AndroidProjects)에서 진행한 Recoder의 기존 기능을 보완하고 새로운 기능을 추가한 프로젝트입니다.   

<br>

## 📋 프로젝트 소개

- ✈️ 친구, 연인 또는 가족끼리 추억을 회상하는 추억 공유 앨범 앱입니다.
- 💑 특정 사람들과 어떤 추억이 있었는지 시간 순으로 볼 수 있습니다.
- 📷 앨범마다 친구와 사진을 쉽게 공유할 수 있습니다.

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

- Front : Android Studio(Iguana)
- Back-end : Spring Boot(3.3.3)
- Database : MySQL(8.0.32), Redis
- 버전 및 이슈 관리 : Git, Github

### 📅 개발 기간

- 전체 개발 기간 : 2024.08.19 ~ 2024.11.14
- UI 구현 : 2024.08.19 ~ 2024.09.30
- 기능 구현 : 2024.08.31 ~ 2024.09.30
- 테스트 : 2024.10.01 ~ 2024.10.21
- [Gantt Chart](https://github.com/HGCQ/Recoder/blob/main/data/XLGantt_v5.0.0_20220605_Release_KOR.xlsm)

### 🖊️ 브랜치 전략

- **main** : 배포할 때만 사용하는 브랜치입니다.
- **develop** : 개발 단계에서 main 역할을 하는 브랜치입니다.
- **feature** : 하나의 기능을 개발하는 브랜치입니다.

<br>

## 🧩 역할 분담

| 이름 | 역할 |
| :---: | --- |
| 👨‍🎓<br>김명준 | 서버 Rest API 개발(그룹 기능, 앨범 기능, 사진 기능), 프로그램 및 DB 설계 |
| 👨‍🎓<br>김남규 | 안드로이드 Activity 개발(개인), 안드로이드 내부 DB 개발 |
| 👨‍🎓<br>이동현 | 안드로이드 Activity 개발(공유), 클라이언트와 서버 Http 통신 연결 |
| 👨‍🎓<br>전경섭 | 서버 Rest API 개발(회원 기능) |
| 👩‍🎓<br>조서윤 | UI 설계, 안드로이드 View 및 Activity 개발, PPT 및 자료 제작 |

<br>

## 💡 기능 목록
> 기존에는 앨범마다 사람을 직접 초대했으나, 앨범의 상위 그룹으로 그룹을 만들어 미리 사람을 초대 후 그룹마다 앨범을 생성하는 식으로 변경하였습니다.   

- 기존 기능들
    * 회원 기능
        * 회원 가입
        * 로그인 / 로그아웃
        * 회원 정보 수정
        * 팔로우 추가 및 삭제

    * 앨범 기능
        * 앨범 생성
        * 앨범 삭제
        * 앨범 수정
        * 앨범 조회

    * 사진 기능 -> 기존에는 서버 외부 디렉터리에 저장하였으나 AWS로 변경하였습니다.
        * 사진 업로드
        * 사진 삭제
        * 사진 조회
        ```
        /* 변경 전 */   
        @Configuration   
        public class PhotoConfig implements WebMvcConfigurer {   
            @Override   
            public void addResourceHandlers(ResourceHandlerRegistry registry) {   
                registry.addResourceHandler("/images/**")   
                        .addResourceLocations("file:///D:/app/images/");   
            }   
        }   
   
        /* 변경 후 */   

        ```

- 추가된 기능들
    * 그룹 기능
        * 그룹 생성
        * 그룹 삭제
        * 회원 초대

<br>

## 엔티티와 테이블

<br>

### 엔티티 분석
![Entity](https://github.com/HGCQ/Recoder/blob/main/data/Entity.png)

<br>

### 테이블 설계
![Table](https://github.com/HGCQ/Recoder/blob/main/data/table.png)

<br>

## 🔧 페이지별 기능
> 기존에는 사람들과 공유하는 페이지만 있었지만, 혼자서 사용할 수 있는 개인 앨범 페이지를 추가하였습니다.   

<br>

## 프로젝트 후기

### 👦 김명준

<br>

### 👦 김남규

<br>

### 👦 이동현

<br>

### 👦 전경섭

<br>

### 👧 조서윤
