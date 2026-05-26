# [EX_답] 사원입사 -> 그룹웨어 계정발급 — 시험용 정답 (Assessment Answer)

> **NCS 능력단위** `2001020212_23v6` 인터페이스 구현
> **분류**: 능력평가 본 시험 (정답본)
>
> 학생용 [EX](../EX/) 의 정답 + 한국어 주석 버전이다.

---

## 시험 시나리오

| 항목 | 내용 |
|:---|:---|
| 도메인 | HR — 신규 사원 입사등록 시 그룹웨어 계정 자동 발급 |
| 패턴 | Transactional Outbox + Inbox (DB 테이블 큐, 비동기 배치) |
| 송신 시스템 | HRM (HrmDB: `employee`, `if_outbox`) |
| 수신 시스템 | Groupware (GroupwareDB: `dept`, `account`, `account_history`, `if_inbox`) |
| 인터페이스 ID | IF_HR_001 |
| 멱등키 | `tx_no` = `emp_id` |
| 보안 | 비밀번호 SHA-256 해시 저장 + PreparedStatement + 트랜잭션 |

---

## 프로젝트 구조

```
EX_답/
├── docs/인터페이스설계서.md       (정답의 핵심 산출물)
├── db/schema.sql                  (HrmDB + GroupwareDB)
├── config/db.properties.sample
├── src/
│   ├── common/                   DBManager, JsonUtil, Filter, HashUtil(SHA-256)
│   ├── send/
│   │   ├── Employee.java        VO
│   │   ├── EmployeeDAO.java     employee + outbox 트랜잭션
│   │   ├── OutboxDAO.java
│   │   └── EmpRegistServlet.java
│   ├── recv/
│   │   ├── AccountDAO.java      비번 SHA-256 + 부서 FK 검증
│   │   ├── InboxDAO.java        UNIQUE 멱등키
│   │   └── AccountBatchServlet.java   outbox polling
│   └── monitor/
├── WebContent/
└── README.md / CHECKLIST.md
```

---

## 빠른 시작

```cmd
mysql -uroot -p < db\schema.sql
copy config\db.properties.sample config\db.properties
```

Eclipse Import -> Run on Server (Tomcat 10.1) -> <http://localhost:8080/IfApp_EX/>

---

## 시연 시나리오 (TC01~TC08)

| TC | 행위 | 기대 |
|:--:|:---|:---|
| TC01 | 정상 입사등록 (D001, 사원) | outbox=S, inbox=S, account 생성 |
| TC02 | 같은 empId 강제 중복 | inbox UNIQUE -> outbox=S(SKIP) |
| TC03 | D999 (없는 부서) | outbox=F (E30) |
| TC04 | 직급 "인턴" | 송신측 즉시 거부 (E31) |
| TC05 | empName 빈 값 | 송신측 거부 (E10) |
| TC06 | 다수 등록 후 배치 일괄 | 모두 처리 |
| TC07 | TC03 재시도 3회 | retry_cnt=3 -> 더이상 잡히지 않음 |
| TC08 | TC03 의 D999를 DB 추가 후 [재시도] | outbox N->S 전환 |

자세한 평가 기준 -> [CHECKLIST.md](CHECKLIST.md)
