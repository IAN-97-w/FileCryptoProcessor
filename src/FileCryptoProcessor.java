import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * packageName    : PACKAGE_NAME
 * fileName       : FileCryptoProcessor
 * author         : wonchang
 * date           : 25. 6. 20.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 6. 20.        wonchang       최초 생성
 */
public class FileCryptoProcessor {

    private static final String SECRET = "1234567890123456";

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("사용법: java FileCryptoProcessor [enc|dec] <sourceDir> <targetDir>");
            return;
        }

        String mode = args[0];
        String sourceDir = args[1];
        String targetDir = args[2];

        if (mode.equalsIgnoreCase("enc")) {
            encFiles(sourceDir, targetDir);
        } else if (mode.equalsIgnoreCase("dec")) {
            decFiles(sourceDir, targetDir);
        } else {
            System.out.println("모드가 잘못되었습니다. enc 또는 dec 중 하나를 선택하세요.");
        }
    }

    private static void encFiles(String sourceDir, String targetDir) throws Exception {
        Path sourcePath = Paths.get(sourceDir);
        Path targetPath = Paths.get(targetDir);

        Files.walk(sourcePath).forEach(path -> {
            try {
                if (Files.isRegularFile(path)) {
                    byte[] raw = Files.readAllBytes(path);
                    byte[] encRaw = encodeFile(raw);

                    Path relativePath = sourcePath.relativize(path);
                    Path parentPath = relativePath.getParent();
                    Path encDirPath = (parentPath == null) ? Paths.get("") : encodePath(parentPath);

                    String encNm = encodeName(path.getFileName().toString()) + ".log";
                    Path outPath = targetPath.resolve(encDirPath).resolve(encNm);

                    Files.createDirectories(outPath.getParent());
                    Files.write(outPath, encRaw);
                }
            } catch (Exception e) {
                System.err.println("암호화 실패: " + path.toString());
                e.printStackTrace();
            }
        });
    }

    private static void decFiles(String sourceDir, String targetDir) throws Exception {
        Path sourcePath = Paths.get(sourceDir);
        Path targetPath = Paths.get(targetDir);

        Files.walk(sourcePath).forEach(path -> {
            try {
                if (Files.isRegularFile(path)) {
                    byte[] raw = Files.readAllBytes(path);
                    byte[] decRaw = decodeFile(raw);

                    Path relativePath = sourcePath.relativize(path);
                    Path parentPath = relativePath.getParent();
                    Path decDirPath = (parentPath == null) ? Paths.get("") : decodePath(parentPath);

                    String decNm = decodeName(path.getFileName().toString());
                    Path outPath = targetPath.resolve(decDirPath).resolve(decNm);

                    Files.createDirectories(outPath.getParent());
                    Files.write(outPath, decRaw);
                }
            } catch (Exception e) {
                System.err.println("복호화 실패: " + path.toString());
                e.printStackTrace();
            }
        });
    }

    private static Path encodePath(Path path) throws Exception {
        Path encoded = Paths.get("");
        for (Path part : path) {
            encoded = encoded.resolve(encodeName(part.toString()));
        }
        return encoded;
    }

    private static Path decodePath(Path path) throws Exception {
        Path decoded = Paths.get("");
        for (Path part : path) {
            decoded = decoded.resolve(decodeName(part.toString()));
        }
        return decoded;
    }

    private static byte[] encodeFile(byte[] data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    private static byte[] decodeFile(byte[] data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    private static String encodeName(String name) throws Exception {
        byte[] nameBytes = name.getBytes("UTF-8");
        return Base64.getUrlEncoder().encodeToString(nameBytes);
    }

    private static String decodeName(String text) throws Exception {
        String realText = text.replace(".log", "");
        byte[] decText = Base64.getUrlDecoder().decode(realText);
        return new String(decText, "UTF-8");
    }
}
