package projek.projekpbo;

//Import tools yang dibutuhkan
import helpers.DbConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuStudentController {
    ObservableList<PesananMahasiswa> pesananMahasiswa = FXCollections.observableArrayList();
    String query = null;
    Connection connection = null;
    PreparedStatement preparedStatement = null ;
    ResultSet resultSet = null ;
    String Status = "Menunggu";

    //Method menampilkan nama berdasarkan NIM dari database ke scene Menu Mahasiswa
    public void displayName(String InputNIM, String InputPass){
        Connection con;
        PreparedStatement pst;
        ResultSet rs;

        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/dbbook", "root", "");
            pst = con.prepareStatement("select * from users where nim=?");

            pst.setString(1, InputNIM);

            rs = pst.executeQuery();

            if(rs.next() == false) {
                namaLabel.setText("");
                NIMLabel.setText("");
                NIMLabel.requestFocus();
            }

            else{
                namaLabel.setText(rs.getString("nama"));
                NIMLabel.setText(rs.getString("nim"));
            }

        }
        catch(ClassNotFoundException ex){

        }
        catch (SQLException ex){

        }
    }

    //Memanggil FXML yang telah dibuat di Scene Builder
    @FXML
    private TableView<PesananMahasiswa> tableView;

    @FXML
    public TableColumn<PesananMahasiswa, String> kolomNIM;

    @FXML
    public TableColumn<PesananMahasiswa, String> kolomTime;

    @FXML
    public TableColumn<PesananMahasiswa, String> kolomKelas;
    @FXML
    public TableColumn<PesananMahasiswa, String> kolomMulai;
    @FXML
    public TableColumn<PesananMahasiswa, String> kolomSelesai;

    @FXML
    public TableColumn<PesananMahasiswa, String> kolomStatus;
    @FXML
    public TableColumn<PesananMahasiswa, String> kolomSelect;
    int studentId;

    //List nama kelas dan kapasitas kelas untuk drop down pilih kelas
    ObservableList<String> pilihKelasList = FXCollections.observableArrayList("G10 CR50", "123 CR50" ,"203 CR100", "208 CR50", "318 CR100");

    private Stage stage;
    @FXML
    ChoiceBox<String> pilihKelasBox;
    @FXML
    DatePicker tanggalKelas;
    @FXML
    TextField fieldMulaiKelas;

    @FXML
    TextField fieldSelesaiKelas;
    @FXML
    Label namaLabel;
    @FXML
    Label NIMLabel;

    //Query untuk memasukkan data ke database
    private void getQuery() {
        query = "INSERT INTO `tablebooking`(`NIM`, `kelas`, `tanggal`, `mulai`, `selesai`, `status`) VALUES (?,?,?,?,?,?)";
    }

    //Untuk memasukkan inputan user ke database
    public void insert() {

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, NIMLabel.getText());
            preparedStatement.setString(2, pilihKelasBox.getValue());
            preparedStatement.setString(3, String.valueOf(tanggalKelas.getValue()));
            preparedStatement.setString(4, fieldMulaiKelas.getText());
            preparedStatement.setString(5, fieldSelesaiKelas.getText());
            preparedStatement.setString(6, Status);
            preparedStatement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(MenuStudentController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    //Inisialisasi untuk load data dan drop down pilih kela
    public void initialize(){
        loadData();
        pilihKelasBox.setItems(pilihKelasList);
    }

    //Refresh data untuk memunculkan data terbaru
    public void refreshData(){
        try {
            pesananMahasiswa.clear();
            query = "SELECT * FROM `tablebooking` WHERE NIM ='"+NIMLabel.getText()+"'";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                pesananMahasiswa.add(new PesananMahasiswa(
                        resultSet.getInt("id"),
                        resultSet.getString("NIM"),
                        resultSet.getString("kelas"),
                        resultSet.getString("tanggal"),
                        resultSet.getString("mulai"),
                        resultSet.getString("selesai"),
                        resultSet.getString("status"),
                        resultSet.getString("pilih")));
                tableView.setItems(pesananMahasiswa);
            }
        }catch (SQLException ex) {
            Logger.getLogger(MenuStudentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Pengecekan apakah suatu kelas telah dipesan pada waktu tertentu berdasarkan input user
    public void checkData(){
        try {
            pesananMahasiswa.clear();
            query = "SELECT * FROM `tablebooking` WHERE ((kelas ='"+pilihKelasBox.getValue()+"') AND (tanggal= '"+tanggalKelas.getValue()+"') AND ((mulai >= '"+fieldMulaiKelas.getText()+":00' AND mulai <= '"+fieldSelesaiKelas.getText()+":00') OR (selesai >= '"+fieldMulaiKelas.getText()+":00' AND selesai <= '"+fieldSelesaiKelas.getText()+":00')))";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Ruang kelas tidak tersedia");
                alert.showAndWait();
            }else{
                getQuery();
                insert();
            }
        }catch (SQLException ex) {
            Logger.getLogger(MenuStudentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Menampilkan data pada tabel yang ada di tampilan (GUI)
    public void loadData(){
        connection = DbConnect.getConnect();
        refreshData();
        kolomNIM.setCellValueFactory(new PropertyValueFactory<>("NIM"));
        kolomKelas.setCellValueFactory(new PropertyValueFactory<>("kelas"));
        kolomTime.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        kolomMulai.setCellValueFactory(new PropertyValueFactory<>("mulai"));
        kolomSelesai.setCellValueFactory(new PropertyValueFactory<>("selesai"));
        kolomStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        kolomSelect.setCellValueFactory(new PropertyValueFactory<>("pilihBaris"));
    }

    //Tombol untuk memesan kelas
    public void buttonPesanKelas (ActionEvent event) throws IOException{
        connection = DbConnect.getConnect();
        String tanggal = String.valueOf(tanggalKelas.getValue());
        String mulai = fieldMulaiKelas.getText();
        String selesai = fieldSelesaiKelas.getText();


        if (tanggal.isEmpty() || mulai.isEmpty() || selesai.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Masukkan semua data!");
            alert.showAndWait();

        } else {
            checkData();
        }
        refreshData();
    }

    private String studentNIM;
    private String studentKelas;
    private String studentTanggal;
    private String studentMulai;
    private String studentSelesai;
    private String studentStatus;

    //Pembuatan parameter agar fungsi checkbox dapat mendeteksi data hanya pada keadaan checkbox di check atau dicentang
    void setTextField2(int id, String NIM, String kelas, String tanggal, String mulai, String selesai, String status) {
        studentId = id;
        studentNIM = NIM;
        studentKelas = kelas;
        studentTanggal = tanggal ;
        studentMulai = mulai;
        studentSelesai = selesai;
        studentStatus = status;
    }

    //Tombol untuk membatalkan pesanan
    @FXML
    public void buttonBatalkan(ActionEvent event) {
        try {
            ObservableList<PesananMahasiswa> dataListHapus = FXCollections.observableArrayList();

            for (PesananMahasiswa hapus : pesananMahasiswa) {
                if (hapus.getPilihBaris().isSelected()) {
                    dataListHapus.add(hapus);
                    setTextField2(hapus.getId(), hapus.getNIM(), hapus.getKelas(), hapus.getTanggal(), hapus.getMulai(), hapus.getSelesai(), hapus.getStatus());
                    query = "DELETE FROM `tablebooking` WHERE id  ="+hapus.getId();
                    connection = DbConnect.getConnect();
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.execute();
                }
            }
            refreshData();

        } catch (Exception ex) {
            Logger.getLogger(MenuStudentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Tombol untuk log out dari menu student
    public void switchToLogOutStudent(ActionEvent event) throws IOException{
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Keluar");
        alert.setHeaderText(null);
        alert.setContentText("Anda yakin ingin keluar?");

        if (alert.showAndWait().get() == ButtonType.OK){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainMenu.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Room Booking Portal Universitas Hasanuddin");
            Image icon = new Image("D:\\Coding\\Sem 3\\Java\\UNHAS-booking-room-main\\src\\main\\resources\\projek\\images\\Logo-Resmi-Unhas-1.png");
            stage.getIcons().add(icon);
            stage.setTitle("Room Booking Portal Universitas Hasanuddin");
            stage.setScene(scene);
            stage.show();
        }
        else{
            event.consume();
        }
    }
}
