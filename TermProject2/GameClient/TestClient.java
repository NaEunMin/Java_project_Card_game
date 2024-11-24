package GameClient;

import java.io.*;
import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TestClient extends JFrame {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private JTextArea chatArea;
    private JTextField chatInput;
    private String clientName;

    private CardLayout cardLayout; // 화면 전환을 위한 레이아웃
    private JPanel mainPanel; // CardLayout이 적용된 메인 패널

    public TestClient() throws IOException {
        renderUI(); // UI 생성
        initClient(); // 클라이언트 초기화
    }

    private void initClient() throws IOException {
        socket = new Socket("127.0.0.1", 30000);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        clientName = JOptionPane.showInputDialog(this, "사용자 이름을 입력하세요:");
        if (clientName == null || clientName.trim().isEmpty()) {
            clientName = "Unknown";
        }

        out.writeUTF(clientName); // 서버에 이름 전달
        startReceiver(); // 수신 스레드 시작
    }

    private void sendMessage(String message) {
        try {
            if (message.trim().isEmpty()) return;
            out.writeUTF(message); // 메시지 서버로 전송
            chatArea.append("[나] " + message + "\n");
            chatInput.setText(""); // 입력 창 비우기
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReceiver() {
        Thread receiver = new Thread(() -> {
            try {
                while (true) {
                    String msg = in.readUTF(); // 서버에서 메시지 수신
                    System.out.println("서버 메시지: " + msg);

                    if ("게임 시작".equals(msg)) {
                        SwingUtilities.invokeLater(this::showGameScreen); // "게임 시작" 화면으로 전환
                    } else {
                        chatArea.append(msg + "\n"); // 일반 채팅 메시지 처리
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "서버 연결이 끊어졌습니다.", "연결 끊김", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        });
        receiver.start();
    }

    private void showGameScreen() {
        cardLayout.show(mainPanel, "게임 화면"); // "게임 화면"으로 전환
    }

    private void renderUI() {
        setTitle("클라이언트 채팅");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 채팅 화면
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea(10, 40);
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);

        chatInput = new JTextField(30);
        JButton sendButton = new JButton("전송");

        chatInput.addActionListener((ActionEvent e) -> sendMessage(chatInput.getText()));
        sendButton.addActionListener((ActionEvent e) -> sendMessage(chatInput.getText()));

        JPanel gameButtonPanel = new JPanel();
        gameButtonPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton blackjackButton = new JButton("블랙잭");
        JButton indianPokerButton = new JButton("인디언 포커");
        JButton oneCardButton = new JButton("원카드");

        blackjackButton.addActionListener(e -> sendMessage("매칭 요청: 블랙잭"));
        indianPokerButton.addActionListener(e -> sendMessage("매칭 요청: 인디언 포커"));
        oneCardButton.addActionListener(e -> sendMessage("매칭 요청: 원카드"));

        gameButtonPanel.add(blackjackButton);
        gameButtonPanel.add(indianPokerButton);
        gameButtonPanel.add(oneCardButton);

        JPanel inputPanel = new JPanel();
        inputPanel.add(chatInput);
        inputPanel.add(sendButton);

        chatPanel.add(chatScroll, BorderLayout.NORTH);
        chatPanel.add(gameButtonPanel, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        mainPanel.add(chatPanel, "채팅 화면");

        // 게임 화면
        JPanel gamePanel = new JPanel(new BorderLayout());
        JLabel gameLabel = new JLabel("Game Start!", SwingConstants.CENTER);
        gameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JButton backButton = new JButton("돌아가기");

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "채팅 화면"));

        gamePanel.add(gameLabel, BorderLayout.CENTER);
        gamePanel.add(backButton, BorderLayout.SOUTH);

        mainPanel.add(gamePanel, "게임 화면");

        getContentPane().add(mainPanel);
        cardLayout.show(mainPanel, "채팅 화면");

        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            new TestClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
