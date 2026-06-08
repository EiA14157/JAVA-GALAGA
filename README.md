# Java Galaga

Swing으로 만든 간단한 갤러그 스타일 게임입니다.

## 포함된 기능

- 시작 화면
- 플레이어 좌우 이동
- 스페이스바 발사
- 적 편대 이동
- 적 총알 공격
- 충돌 판정과 점수 시스템
- 3스테이지 진행
- 게임 오버 / 클리어 화면

## 폴더 구조

```text
src/
  galaga/
    Main.java
    GameFrame.java
    GamePanel.java
    GameState.java
    Sprite.java
    Player.java
    Enemy.java
    Bullet.java
```

## 실행 방법

JDK가 설치되어 있으면 아래처럼 컴파일하고 실행할 수 있습니다.

```bash
javac -d out src/galaga/*.java
java -cp out galaga.Main
```

Windows PowerShell에서는 다음처럼 실행해도 됩니다.

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out src/galaga/*.java
java -cp out galaga.Main
```
