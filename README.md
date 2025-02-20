<div align="center">

<!-- logo -->
<img src="https://user-images.githubusercontent.com/80824750/208554611-f8277015-12e8-48d2-b2cc-d09d67f03c02.png" width="400"/>

PPT 완성되면 지수님께서 작업하신 메인 페이지 여기 넣어도 예쁠 것 같아요

[<img src="https://img.shields.io/badge/-readme.md-important?style=flat&logo=google-chrome&logoColor=white" />]() [<img src="https://img.shields.io/badge/release-v0.0.0-yellow?style=flat&logo=google-chrome&logoColor=white" />]() 
<br/> [<img src="https://img.shields.io/badge/프로젝트 기간-2025.02.04~2025.02.21-green?style=flat&logo=&logoColor=white" />]()

</div> 

<br/>

# 1. Project Overview (프로젝트 개요)
- 프로젝트 이름: 찾아줘 ~ DangDangCrew !
- 프로젝트 설명: 카카오 지도 API를 활용한 반려동물 동반자 찾기 플랫폼

<br/>
<br/>

# 2. Team Members (팀원 및 팀 소개)
<table>
  <tr>
    <td align="center">
      <a href="https://github.com/ijnim1121">
        <img src="https://github.com/ijnim1121.png?size=100" width="100px;" alt="김지민"/>
        <br />
        <sub><b>김지민</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/sukangpunch">
        <img src="https://github.com/sukangpunch.png?size=100" width="100px;" alt="강형준"/>
        <br />
        <sub><b>강형준</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/leedidu">
        <img src="https://github.com/leedidu.png?size=100" width="100px;" alt="이지수"/>
        <br />
        <sub><b>이지수</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/youngkwanglim">
        <img src="https://github.com/youngkwanglim.png?size=100" width="100px;" alt="임영광"/>
        <br />
        <sub><b>임영광</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/Jung-Taemin">
        <img src="https://github.com/Jung-Taemin.png?size=100" width="100px;" alt="정태민"/>
        <br />
        <sub><b>정태민</b></sub>
      </a>
    </td>
  </tr>
  <tr>
    <td align="center">BE</td>
    <td align="center">BE</td>
    <td align="center">BE</td>
    <td align="center">BE</td>
    <td align="center">BE</td>
  </tr>
</table>

<br/>

# 3. Key Features (주요 기능)
- **회원가입**:
  - 회원가입 시 MySQL에 유저정보가 등록됩니다.

- **로그인**:
  - 사용자의 인증 정보를 검증하여 로그인합니다.
  - 인증이 완료되면 Access Token과 Refresh Token이 발급됩니다.
  - Access Token은 클라이언트가 서버에 요청할 때 인증에 사용됩니다.
  - Refresh Token은 Redis에 저장되며, Access Token이 만료될 경우 새로운 Access Token을 발급받는 데 사용됩니다.
 
- **로그아웃**:
  - 사용자 인증을 해제하고, 세션을 종료합니다.
  - Redis에서 저장된 RefreshToken이 삭제되어 더 이상 액세스 토큰을 갱신할 수 없습니다.

- **모임 생성**:
  - 카카오 맵을 통해 사용자가 지정한 위치에 해당하는 애견 동반 장소를 보여줍니다.
  - 애견 동반 장소를 클릭하여 장소 상세 정보를 확인하고 모임 생성 버튼을 통해 모임을 생성합니다.
  - 모임 생성시에 최대 인원수, 모임 설명, 모임 날짜 등의 내용을 기입하여 모임을 생성합니다.

- **모임 참가**:
  - 사용자가 지도 혹은 즐겨찾기 알림을 통해 생성된 모임을 확인합니다.
  - 모임 소개글에 적힌 장소, 시간등을 확인 합니다.
  - 참가를 원할 경우 참가 신청을 누릅니다.
 
- **실시간 채팅 기능**:
  - 실시간으로 여러 사람들과 채팅방에서 모임 관련 대화를 할 수 있습니다.
 
- **참여자 평가**:
  - 모임이 종료되면 해댱 모임 참여차들 평가를 선택적으로 할 수 있습니다.

- **즐겨찾기**:
  - 모임을 희망하는 장소를 즐겨찾기 등록합니다.
  - 즐겨찾기 등록한 장소에 모임이 생성되면 알림을 전송합니다.

- **핫플 추천 알림**:
  - 장소에 생성된 모임의 확정된 참가자들 수 기반으로 일정 수치 이상의 참가자수가 발생하면 접속 유저들에게 핫플 장소 알림(SSE)를 전송합니다.

<br/>
<br/>

# 4. Tasks & Responsibilities (작업 및 역할 분담)
<table>
  <tr>
    <th align="center">작업 유형</th>
    <th align="center">담당자</th>
    <th align="center">내용</th>
  </tr>
  <tr>
    <td rowspan="3" align="center" colspan="2"><b>공통 작업</b></td>
    <td>- 테스트 코드</td>
  </tr>
  <tr>
    <td>- ERD 설계</td>
  </tr>
  <tr>
    <td>- 요구사항 정리 및 사용자 시나리오 작성</td>
  </tr>
  <tr>
    <td rowspan="5" align="center"><b>개별 작업</b></td>
    <td align="center">
      <a href="https://github.com/ijnim1121">
        <img src="https://github.com/ijnim1121.png?size=50" width="50px"><br/>
        <b>김지민</b>
      </a>
    </td>
    <td>
      <b>유저 도메인</b><br/>
      - 회원가입/로그인<br/>
      - 보안, 인가, 인증 적용 (JWT)
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/Jung-Taemin">
        <img src="https://github.com/Jung-Taemin.png?size=50" width="50px"><br/>
        <b>정태민</b>
      </a>
    </td>
    <td>
      <b>유저 평가 도메인</b><br/>
      - 평점<br/>
      - 평가 내용
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/sukangpunch">
        <img src="https://github.com/sukangpunch.png?size=50" width="50px"><br/>
        <b>강형준</b>
      </a>
    </td>
    <td>
      <b>장소 도메인</b><br/>
      <b>알림 도메인</b><br/>
      - 실시간 인기 정보 알림 시스템 적용 (SSE)<br/>
      - 배포 및 CI/CD 구축<br/>
      - 모니터링 서버 구축
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/leedidu">
        <img src="https://github.com/leedidu.png?size=50" width="50px"><br/>
        <b>이지수</b>
      </a>
    </td>
    <td>
      <b>모임 도메인</b><br/>
      - 모임 상세페이지<br/>
      - 개인 모임 조회<br/>
      - 프로토타입 제작
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/youngkwanglim">
        <img src="https://github.com/youngkwanglim.png?size=50" width="50px"><br/>
        <b>임영광</b>
      </a>
    </td>
    <td>
      <b>채팅 도메인</b><br/>
      - 장소 별 채팅 기능 적용 (WebSocket)<br/>
      - 프로토타입 제작
    </td>
  </tr>
</table>

<br/>

# 5. Technology Stack (기술 스택)
### Back-end
<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Java.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringBoot.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringSecurity.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringDataJPA.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/JWT.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Mysql.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/MongoDB.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Redis.png?raw=true" width="80">

</div>

### Infra
<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/AWSEC2.png?raw=true" width="80">
</div>

### Tools
<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Github.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Notion.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Docker.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Figma.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Postman.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Swagger.png?raw=true" width="80">


</div>

<br />


# 6. Project Structure (프로젝트 구조)
```bash
src
└── main
    ├── java
    │   └── findme.dangdangcrew
    │       ├── chat
    │       ├── evaluation
    │       ├── global
    │       │   ├── config
    │       │   ├── dto
    │       │   ├── entity
    │       │   ├── exception
    │       │   ├── interceptor
    │       │   ├── publisher
    │       │   ├── service
    │       ├── meeting
    │       ├── notification
    │       ├── place
    │       ├── sse
    │       ├── user
    │       │   ├── controller
    │       │   ├── dto
    │       │   ├── entity
    │       │   ├── repository
    │       │   ├── service
    │       └── DangdangcrewApplication
    ├── resources
    │   └── application.yml

```

<br/>
<br/>

# 7. ProtoType (프로토타입)

# 8. Development Workflow (개발 워크플로우)
## 브랜치 전략 (Branch Strategy)
- dev Branch
  - 배포 가능한 상태의 코드를 유지합니다.
  - 모든 배포는 이 브랜치에서 이루어집니다.
  
- feature Branch
  - 팀원 각자의 개발 브랜치입니다.
  - 모든 기능 개발은 이 브랜치에서 이루어집니다.
  - 브랜치명: 태그-#이슈번호-개발내용
    - ex) feat-#14-user-api

<br/>
<br/>

# 9. Git Convention (깃 컨벤션)

## Label

| 이모지 | 태그 | 설명 |
|:------:|:------:|:------------------------------------------------|
| ⚙️ | `build` | 빌드 관련 파일 수정 |
| ✅ | `ci/cd` | CI/CD 설정 파일 수정 |
| ✍️ | `comment` | 필요한 주석 추가 및 변경 |
| 📘 | `docs` | 문서를 개선하거나 내용을 추가 |
| ✏️ | `style` | 코드 포맷 변경, 세미콜론 누락 등 코드 변경 없음 |
| ✨ | `feat` | 새로운 기능을 추가할 경우 |
| ❌ | `remove` | 파일을 삭제하는 작업만 수행한 경우 |
| 🎨 | `refactor` | 프로덕션 코드 리팩토링 |
| 💡 | `rename` | 파일 혹은 폴더명을 수정하거나 이동한 경우 |
| 💡 | `test` | 테스트 추가, 테스트 리팩토링 (프로덕션 코드 변경 X) |
| 🔨 | `fix` | 버그를 고친 경우 |

<br/>

## ISSUE TEMPLATE
이슈명: 내용
<br/>

ex) 유저 CRUD 기능 추가
```
## 이슈
### 현재 상황
- 현재 발생한 문제나 개선이 필요한 상황을 설명해주세요.

### 목표
- 이슈를 통해 달성하고자 하는 목표를 설명해주세요.

## 작업 내용
- [ ] 작업 1
- [ ] 작업 2
- [ ] 작업 3

## 스크린샷
필요한 경우 스크린샷을 첨부해주세요.

## 참고자료
관련 문서나 링크를 첨부해주세요.
```

<br/>
<br/>

## PR TEMPLATE
PR명: [#이슈번호] 내용
<br/>

ex) [#14] 유저 CRUD 기능 추가
```
### PR 타입(하나 이상의 PR 타입을 선택해주세요)
- [X] 기능 추가
- [ ] 기능 삭제
- [ ] 버그 수정
- [ ] 문서 수정
- [ ] 코드 리팩토링
- [ ] 테스트 코드 추가 및 리팩토링
- [ ] 의존성, 환경 변수, 빌드 관련 코드 업데이트

### 반영 브랜치
ex) dev/feature/join

### 이슈
[#이슈 번호](이슈 링크)

### 변경 사항
ex) 로그인 시, 구글 소셜 로그인 기능을 추가했습니다. (구체적으로, 명시적으로 작성)

### 테스트 결과
ex) 베이스 브랜치에 포함되기 위한 코드는 모두 정상적으로 동작해야 합니다. 결과물에 대한 스크린샷, GIF, 혹은 라이브 데모가 가능하도록 샘플API를 첨부할 수도 있습니다.
```

<br/>
