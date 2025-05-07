import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

public class Main{

    private static final String url = "jdbc:mysql://localhost:3306/hoteldb";
    private static final String username = "root";
    private static final String password = "Atharve@10";

    public static void main(String[] args)throws ClassNotFoundException, SQLException {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection con = DriverManager.getConnection(url, username, password);
            Statement stmt = con.createStatement();

            while(true){
                System.out.println();
                System.out.println("\n--- Welcome to Hotel Management System ---\n");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                int choose = scanner.nextInt();

                switch(choose){
                    case 1:
                        reserveRoom(scanner, stmt);
                        break;
                    case 2:
                        viewReservation(scanner, stmt);
                        break;
                    case 3:
                        getRoomNum(scanner, stmt);
                        break;
                    case 4:
                        updateReservation(scanner, stmt);
                        break;
                    case 5:
                        deleteReservation(scanner, stmt);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    case 112:
                        adminDashboard(stmt);
                        break;
                    default:
                        System.out.println("INVALID CHOICE. TRY AGAIN");
                }

            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Scanner scanner, Statement stmt){
        try{
            System.out.println("ENTER YOUR NAME : ");
            String name = scanner.next();
            scanner.nextLine();
            System.out.println("ENTER YOUR CONTACT NUMBER : ");
            String contact = scanner.next();
            System.out.println("ENTER ROOM NUMBER : ");
            int room = scanner.nextInt();

            String query = "INSERT INTO reservation(name, room_number, contact)"+
                    "VALUES ('" + name + "', " + room + ", '" + contact + "')";
            int AffectedRows = stmt.executeUpdate(query);

            if(AffectedRows > 0){
                System.out.println("Reservation Successful");
            }else{
                System.out.println("Reservation Failed");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewReservation(Scanner scanner, Statement stmt)throws SQLException {
        System.out.println("ENTER ROOM NUMBER");
        int room = scanner.nextInt();

        try{
            String query = "SELECT * FROM reservation " +
                    " WHERE room_number = " + room;

            ResultSet rs = stmt.executeQuery(query);

            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (rs.next()) {
                int reservationId = rs.getInt("id");
                String guestName = rs.getString("name");
                int roomNumber = rs.getInt("room_number");
                String contactNumber = rs.getString("contact");
                String reservationDate = rs.getTimestamp("reservation_date").toString();

                // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private static void getRoomNum(Scanner scan, Statement stmt){
        System.out.println("ENTER THE RESERVATION ID");
        int id = scan.nextInt();
        try{
            String query = "SELECT room_number, name FROM reservation WHERE id =" + id;
            ResultSet rs = stmt.executeQuery(query);

            if(rs.next()){
                int num = rs.getInt("room_number");
                String name = rs.getString("name");
                System.out.println("Room number for Reservation ID " + id +
                        " and Guest " + name + " is: " + num);
            }else {
                System.out.println("Reservation not found for the given ID and guest name.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateReservation(Scanner scan, Statement stmt){
        System.out.println("ENTER RESERVATION ID TO UPDATE");
        int id = scan.nextInt();
        scan.nextLine();

        if (!reservationExist(stmt, id)) {
            System.out.println("Reservation not found for the given ID.");
            return;
        }

        System.out.println("ENTER NEW GUEST NAME");
        String name = scan.nextLine();
        System.out.println("ENTER THE ALLOTED ROOM TO NEW GUEST");
        int room = scan.nextInt();
        System.out.println("ENTER NEW GUEST CONTACT NUMBER");
        String contact = scan.next();

        try{
            String query = "UPDATE reservation SET "
                    + "name = '" + name + "', "
                    + "room_number = " + room + ", "
                    + "contact = '" + contact + "' "
                    + "WHERE id = " + id;

            int AffectedRows = stmt.executeUpdate(query);

            if (AffectedRows > 0) {
                System.out.println("Reservation updated successfully!");
            } else {
                System.out.println("Reservation update failed.");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Scanner scan, Statement stmt){
        System.out.println("ENTER RESERVATION ID TO DELETE");
        int id = scan.nextInt();

        if (!reservationExist(stmt, id)) {
            System.out.println("Reservation not found for the given ID.");
            return;
        }

        try{
            String query = "DELETE FROM reservation WHERE id = " + id;
            int AffectedRows = stmt.executeUpdate(query);

            if (AffectedRows > 0) {
                System.out.println("Reservation deleted successfully!");
            } else {
                System.out.println("Reservation deletion failed.");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

    }

    private static boolean reservationExist(Statement stmt, int id){
        try{
            String query = " SELECT id FROM reservation WHERE id = " + id;
            ResultSet rs = stmt.executeQuery(query);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    private static void exit()throws InterruptedException{
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }

    private static void adminDashboard(Statement stmt)throws SQLException{
        try{
            String query = "SELECT * FROM reservation";
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (rs.next()) {
                int reservationId = rs.getInt("id");
                String guestName = rs.getString("name");
                int roomNumber = rs.getInt("room_number");
                String contactNumber = rs.getString("contact");
                String reservationDate = rs.getTimestamp("reservation_date").toString();

                // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
