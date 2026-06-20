# Java Galaga

Java Swing으로 구현한 간단한 갤러그 스타일 아케이드 게임입니다.

## 주요 기능

- 창 크기 조절 가능
- 방향키를 이용한 플레이어 이동
- 스페이스바를 이용한 플레이어 발사
- 적 편대 이동
- 점수, 목숨, 스테이지 UI 표시
- 시작 화면, 게임 오버 화면, 클리어 화면
- 플레이어, 적, 레이저에 PNG 스프라이트 적용
- 우주 배경 이미지 적용

## 프로젝트 구조

```text
assets/
  space_background.png
  player_ship.png
  enemy_bug.png
  player_laser.png
  enemy_laser.png
src/
  galaga/
    Main.java
    GameFrame.java
    GamePanel.java
    GameConfig.java
    GameAssets.java
    GameRenderer.java
    GameSession.java
    EnemyFormationFactory.java
    InputState.java
    GameState.java
    Sprite.java
    Player.java
    Enemy.java
    Bullet.java
```

## 파일 역할

- `Main.java`: Swing 애플리케이션을 시작합니다.
- `GameFrame.java`: 게임 창을 생성하고 `GamePanel`을 표시합니다.
- `GamePanel.java`: 타이머, 키 입력, 화면 렌더링을 연결합니다.
- `GameConfig.java`: 게임 전반에서 사용하는 상수와 설정값을 관리합니다.
- `GameAssets.java`: 스프라이트와 배경 이미지를 불러옵니다.
- `GameRenderer.java`: 현재 게임 상태를 화면에 그립니다.
- `GameSession.java`: 이동, 충돌, 점수, 스테이지 진행 등 핵심 게임 로직을 관리합니다.
- `EnemyFormationFactory.java`: 스테이지에 맞는 적 편대를 생성합니다.
- `InputState.java`: 현재 눌린 키 상태를 추적합니다.
- `GameState.java`: 시작 화면, 플레이 중, 게임 오버 같은 상태를 정의합니다.
- `Sprite.java`: 위치와 충돌 범위를 가지는 기본 객체 클래스입니다.
- `Player.java`: 플레이어 우주선의 이동과 목숨 정보를 관리합니다.
- `Enemy.java`: 적 위치와 행 정보를 관리합니다.
- `Bullet.java`: 총알 이동과 화면 밖 제거 처리를 담당합니다.

## 실행 방법

컴파일:

```bash
javac -encoding UTF-8 -d out src/galaga/*.java
```

실행:

```bash
java -cp out galaga.Main
```

Windows PowerShell 예시:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -encoding UTF-8 -d out src/galaga/*.java
java -cp out galaga.Main
```
