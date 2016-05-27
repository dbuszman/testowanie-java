package com.example.deviceStorageJDBC.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.example.deviceStorageJDBC.domain.Storage;

public class StorageManager {
	
	private Connection connection;

	private String url = "jdbc:hsqldb:hsql://localhost/workdb";

	private String createTableStorage = "CREATE TABLE Storage(id_position BIGINT UNIQUE GENERATED BY DEFAULT AS IDENTITY, name VARCHAR(30), amount INTEGER, margin INTEGER)";

	private PreparedStatement addPositionStmt;
	private PreparedStatement deleteOnePositionStmt;
	private PreparedStatement deleteAllPositionsStmt;
	private PreparedStatement getAllPositionsStmt;
	private PreparedStatement updatePositionsStmt;
	private PreparedStatement countAllPositionsStmt;
	private PreparedStatement getPositionsWithLowAmountStmt;

	private Statement statement;
	
	public StorageManager() {
		try {
			connection = DriverManager.getConnection(url);
			statement = connection.createStatement();

			ResultSet rs = connection.getMetaData().getTables(null, null, null, null);
			boolean tableExists = false;
			while (rs.next()) {
				if ("Storage".equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
					tableExists = true;
					break;
				}
			}

			if (!tableExists)
				statement.executeUpdate(createTableStorage);

			addPositionStmt = connection
					.prepareStatement("INSERT INTO Storage (name, amount, margin) VALUES (?, ?, ?)");
			updatePositionsStmt = connection
					.prepareStatement("UPDATE Storage SET margin = ? WHERE amount < ? and id_position = ?");
			deleteOnePositionStmt = connection
					.prepareStatement("DELETE FROM Storage WHERE id_position = ?");
			deleteAllPositionsStmt = connection
					.prepareStatement("DELETE FROM Storage");
			getAllPositionsStmt = connection
					.prepareStatement("SELECT id_position, name, amount, margin FROM Storage");
			countAllPositionsStmt = connection
					.prepareStatement("SELECT COUNT(*) FROM Storage");
			getPositionsWithLowAmountStmt = connection
					.prepareStatement("Select id_position, name, amount, margin FROM Storage WHERE amount < ?");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	Connection getConnection() {
		return connection;
	}

	public int getCount() {

		try {
			ResultSet result = countAllPositionsStmt.executeQuery();
			if (result.next()) {
				return result.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	int removeOnePosition(Storage position) {

		int count = 0;
		try {
			deleteOnePositionStmt.setLong(1, position.getIdPosition());
			count = deleteOnePositionStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	void removePositions() throws SQLException {

		try {
			connection.setAutoCommit(false);
			deleteAllPositionsStmt.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
		} finally {
			connection.setAutoCommit(true);
		}
	}

	void updatePositions(int margin, int amount) throws SQLException {

		int records = getCount();
		List<Storage> positions = getAllPositions();

		updatePositionsStmt.setInt(1, margin);
		updatePositionsStmt.setInt(2, amount);

		for (int i = 0; i < records; i++) {

			Storage positionRetrieved = positions.get(i);
			updatePositionsStmt.setLong(3, positionRetrieved.getIdPosition());

			try {
				updatePositionsStmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public int addPosition(Storage position) {

		int count = 0;
		try {
			addPositionStmt.setString(1, position.getName());
			addPositionStmt.setInt(2, position.getAmount());
			addPositionStmt.setInt(3, position.getMargin());

			count = addPositionStmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	public List<Storage> getAllPositions() {

		List<Storage> positions = new ArrayList<Storage>();

		try {
			ResultSet rs = getAllPositionsStmt.executeQuery();

			while (rs.next()) {
				Storage position = new Storage();
				position.setIdPosition(rs.getInt("id_position"));
				position.setName(rs.getString("name"));
				position.setAmount(rs.getInt("amount"));
				position.setMargin(rs.getInt("margin"));
				positions.add(position);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return positions;
	}
	
	public List<Storage> getPositionsWithLowAmount(int amount){
		
		List<Storage> positionsWithLowAmount = new ArrayList<Storage>();
		
		try{
			getPositionsWithLowAmountStmt.setInt(1, amount);
			ResultSet rs = getPositionsWithLowAmountStmt.executeQuery();
			while (rs.next()) {
				Storage position = new Storage();
				position.setIdPosition(rs.getInt("id_position"));
				position.setName(rs.getString("name"));
				position.setAmount(rs.getInt("amount"));
				position.setMargin(rs.getInt("margin"));
				positionsWithLowAmount.add(position);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return positionsWithLowAmount;
	}

}
