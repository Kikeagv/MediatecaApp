package com.mediateca;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import com.formdev.flatlaf.FlatDarculaLaf;

public class MediatecaApp {
    public static void main(String[] args) {
    	try {
             UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new MediatecaApp().showMenu());
    }

    private void showMenu() {
        String[] options = {"Agregar Material", "Modificar Material", "Mostrar Materiales", "Borrar Material", "Buscar Material", "Salir"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(null, "Bienvenido a la Mediateca, ¿Qué deseas hacer?", "Mediateca",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0: addMaterial(); break;
                case 1: modifyMaterial(); break;
                case 2: listMaterials(); break;
                case 3: deleteMaterial(); break;
                case 4: searchMaterial(); break;
                case 5: System.exit(0);
                default: break;
            }
        }
    }

    // Clase para verificar que el input sea numérico
    class NumberVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            JTextField textField = (JTextField) input;
            String text = textField.getText();
            try {
                Integer.parseInt(text);  // Intenta convertir el texto a entero
                return true;
            } catch (NumberFormatException e) {
                return false;  // Devuelve falso si no es un número
            }
        }

        @Override
        public boolean shouldYieldFocus(JComponent input) {
            if (!verify(input)) {
                JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }
    }

    private void addMaterial() {
        JDialog dialog = new JDialog((Frame) null, "Agregar Material", true);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Add padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Combobox
        String[] types = {"Books", "Magazines", "CDs", "DVDs"};
        JComboBox<String> typeComboBox = new JComboBox<>(types);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Tipo de material:"), gbc);
        gbc.gridx++;
        panel.add(typeComboBox, gbc);

        // Título
        gbc.gridx = 0;
        gbc.gridy++;
        JTextField titleField = new JTextField(20);
        panel.add(new JLabel("Título:"), gbc);
        gbc.gridx++;
        panel.add(titleField, gbc);

        // Campos dinámicos
        JPanel dynamicFieldsPanel = new JPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(dynamicFieldsPanel, gbc);

        // Default Libros
        addDynamicFields(dynamicFieldsPanel, "Books");

        // Actualizar campos de forma dinámica
        typeComboBox.addActionListener(e -> {
            dynamicFieldsPanel.removeAll();
            addDynamicFields(dynamicFieldsPanel, (String) typeComboBox.getSelectedItem());
            dynamicFieldsPanel.revalidate();
            dynamicFieldsPanel.repaint();
            dialog.pack();
        });

        // Agregar botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Volver al menú");
        JButton submitButton = new JButton("Ingresar");
        dialog.getRootPane().setDefaultButton(submitButton);

        backButton.addActionListener(e -> {
            dialog.dispose();
            showMenu();
        });

        // Añadir botón para agregar el elemento nuevo
        submitButton.addActionListener(e -> handleMaterialSubmission(dialog, typeComboBox, titleField, dynamicFieldsPanel));

        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);

        // Botón para volver
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Aplicar propiedades de la ventana
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(true);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void addDynamicFields(JPanel panel, String type) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Ajustar padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Agregar campos dinámicos basados en el tipo de material
        switch (type) {
            case "Books":
                addModifyField(panel, gbc, "Autor", "");
                addModifyField(panel, gbc, "Páginas", "").setInputVerifier(new NumberVerifier());
                addModifyField(panel, gbc, "Editorial", "");
                addModifyField(panel, gbc, "ISBN", "");
                addModifyField(panel, gbc, "Año de publicación", "").setInputVerifier(new NumberVerifier());
                addModifyField(panel, gbc, "Unidades disponibles", "").setInputVerifier(new NumberVerifier());
                break;
            case "Magazines":
                addModifyField(panel, gbc, "Editorial", "");
                addModifyField(panel, gbc, "Periodicidad", "");
                addModifyField(panel, gbc, "Fecha de publicación (YYYY-MM-DD)", "");
                addModifyField(panel, gbc, "Unidades disponibles", "").setInputVerifier(new NumberVerifier());
                break;
            case "CDs":
                addModifyField(panel, gbc, "Artista", "");
                addModifyField(panel, gbc, "Género", "");
                addModifyField(panel, gbc, "Duración (minutos)", "").setInputVerifier(new NumberVerifier());
                addModifyField(panel, gbc, "Número de canciones", "").setInputVerifier(new NumberVerifier());
                addModifyField(panel, gbc, "Unidades disponibles", "").setInputVerifier(new NumberVerifier());
                break;
            case "DVDs":
                addModifyField(panel, gbc, "Director", "");
                addModifyField(panel, gbc, "Género", "");
                addModifyField(panel, gbc, "Duración (minutos)", "").setInputVerifier(new NumberVerifier());
                break;
        }
    }

    // Método para traer los campos del form de forma dinámica
    private JTextField addModifyField(JPanel panel, GridBagConstraints gbc, String label, String value) {
        gbc.gridx = 0;
        panel.add(new JLabel(label + ":"), gbc);
        gbc.gridx++;
        JTextField textField = new JTextField(value, 20);
        panel.add(textField, gbc);
        gbc.gridy++;
        return textField;
    }
    private void handleMaterialSubmission(JDialog dialog, JComboBox<String> typeComboBox, JTextField titleField, JPanel dynamicFieldsPanel) {
        String type = (String) typeComboBox.getSelectedItem();
        String title = titleField.getText();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Título requerido.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Component[] components = dynamicFieldsPanel.getComponents();
        ArrayList<String> fieldValues = new ArrayList<>();

        for (Component component : components) {
            if (component instanceof JTextField) {
                JTextField field = (JTextField) component;
                String fieldName = ((JLabel) dynamicFieldsPanel.getComponent(dynamicFieldsPanel.getComponentZOrder(field) - 1)).getText();
                String fieldValue = field.getText().trim();

                if (fieldValue.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, fieldName + " es requerido.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if ((fieldName.contains("Pages") || fieldName.contains("Publication Year") || fieldName.contains("Duration") || fieldName.contains("Track Count") || fieldName.contains("Available Units")) && !fieldValue.matches("\\d+")) {
                    JOptionPane.showMessageDialog(dialog, fieldName + " debe ser un número válido.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                fieldValues.add(fieldValue);
            }
        }

        String code = generateNextCode(type);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertSQL = "";
            PreparedStatement pstmt = null;

            switch (type) {
                case "Books":
                    insertSQL = "INSERT INTO Books (code, title, author, pages, publisher, isbn, publication_year, available_units) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    pstmt = conn.prepareStatement(insertSQL);
                    pstmt.setString(1, code);
                    pstmt.setString(2, title);
                    pstmt.setString(3, fieldValues.get(0));
                    pstmt.setInt(4, Integer.parseInt(fieldValues.get(1)));
                    pstmt.setString(5, fieldValues.get(2));
                    pstmt.setString(6, fieldValues.get(3));
                    pstmt.setInt(7, Integer.parseInt(fieldValues.get(4)));
                    pstmt.setInt(8, Integer.parseInt(fieldValues.get(5)));
                    break;
                case "Magazines":
                    insertSQL = "INSERT INTO Magazines (code, title, publisher, periodicity, publication_date, available_units) VALUES (?, ?, ?, ?, ?, ?)";
                    pstmt = conn.prepareStatement(insertSQL);
                    pstmt.setString(1, code);
                    pstmt.setString(2, title);
                    pstmt.setString(3, fieldValues.get(0));
                    pstmt.setString(4, fieldValues.get(1));
                    pstmt.setString(5, fieldValues.get(2));
                    pstmt.setInt(6, Integer.parseInt(fieldValues.get(3)));
                    break;
                case "CDs":
                    insertSQL = "INSERT INTO CDs (code, title, artist, genre, duration, track_count, available_units) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    pstmt = conn.prepareStatement(insertSQL);
                    pstmt.setString(1, code);
                    pstmt.setString(2, title);
                    pstmt.setString(3, fieldValues.get(0));
                    pstmt.setString(4, fieldValues.get(1));
                    pstmt.setInt(5, Integer.parseInt(fieldValues.get(2)));
                    pstmt.setInt(6, Integer.parseInt(fieldValues.get(3)));
                    pstmt.setInt(7, Integer.parseInt(fieldValues.get(4)));
                    break;
                case "DVDs":
                    insertSQL = "INSERT INTO DVDs (code, title, director, genre, duration) VALUES (?, ?, ?, ?, ?)";
                    pstmt = conn.prepareStatement(insertSQL);
                    pstmt.setString(1, code);
                    pstmt.setString(2, title);
                    pstmt.setString(3, fieldValues.get(0));
                    pstmt.setString(4, fieldValues.get(1));
                    pstmt.setInt(5, Integer.parseInt(fieldValues.get(2)));
                    break;
            }

            if (pstmt != null) {
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Material añadido con éxito.");
                dialog.dispose();
                showMenu();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "SQL Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateNextCode(String table) {
        String prefix;
        switch (table) {
            case "Books":
                prefix = "LIB";
                break;
            case "Magazines":
                prefix = "REV";
                break;
            case "CDs":
                prefix = "CDA";
                break;
            case "DVDs":
                prefix = "DVD";
                break;
            default:
                throw new IllegalArgumentException("Unknown table: " + table);
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT code FROM " + table + " ORDER BY code DESC LIMIT 1";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String lastCode = rs.getString("code");
                int numericPart = Integer.parseInt(lastCode.replaceAll("\\D", ""));
                numericPart++;
                return prefix + String.format("%05d", numericPart);
            } else {
                return prefix + "00001";
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private void modifyMaterial() {
        String code = JOptionPane.showInputDialog(null, "Ingresa el código del material a modificar:", "Modificar Material", JOptionPane.QUESTION_MESSAGE);

        if (code == null || code.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Código requerido.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String[] tables = {"Books", "Magazines", "CDs", "DVDs"};
            String tableFound = null;
            ResultSet rs = null;

            // Buscar el material en las tablas
            for (String table : tables) {
                String searchSQL = "SELECT * FROM " + table + " WHERE code = ?";
                PreparedStatement pstmt = conn.prepareStatement(searchSQL);
                pstmt.setString(1, code);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    tableFound = table;
                    break;
                }
            }

            if (tableFound == null) {
                JOptionPane.showMessageDialog(null, "Material no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Mostrar detalles
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JTextField titleField = new JTextField(rs.getString("Title"), 20);
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Título:"), gbc);
            gbc.gridx++;
            panel.add(titleField, gbc);

            ArrayList<JTextField> fieldList = new ArrayList<>();

            switch (tableFound) {
                case "Books":
                    addModifyField(panel, gbc, fieldList, "Autor", rs.getString("author"));
                    addModifyField(panel, gbc, fieldList, "Páginas", String.valueOf(rs.getInt("pages")));
                    addModifyField(panel, gbc, fieldList, "Editorial", rs.getString("publisher"));
                    addModifyField(panel, gbc, fieldList, "ISBN", rs.getString("isbn"));
                    addModifyField(panel, gbc, fieldList, "Año de publicación", String.valueOf(rs.getInt("publication_year")));
                    addModifyField(panel, gbc, fieldList, "Unidades disponibles", String.valueOf(rs.getInt("available_units")));
                    break;
                case "Magazines":
                    addModifyField(panel, gbc, fieldList, "Editorial", rs.getString("publisher"));
                    addModifyField(panel, gbc, fieldList, "Periodicidad", rs.getString("periodicity"));
                    addModifyField(panel, gbc, fieldList, "Fecha de publicación", rs.getString("publication_date"));
                    addModifyField(panel, gbc, fieldList, "Unidades disponibles", String.valueOf(rs.getInt("available_units")));
                    break;
                case "CDs":
                    addModifyField(panel, gbc, fieldList, "Artista", rs.getString("artist"));
                    addModifyField(panel, gbc, fieldList, "Género", rs.getString("genre"));
                    addModifyField(panel, gbc, fieldList, "Duración", String.valueOf(rs.getInt("duration")));
                    addModifyField(panel, gbc, fieldList, "Número de canciones", String.valueOf(rs.getInt("track_count")));
                    addModifyField(panel, gbc, fieldList, "Unidades disponibles", String.valueOf(rs.getInt("available_units")));
                    break;
                case "DVDs":
                    addModifyField(panel, gbc, fieldList, "Director", rs.getString("director"));
                    addModifyField(panel, gbc, fieldList, "Género", rs.getString("genre"));
                    addModifyField(panel, gbc, fieldList, "Duración", String.valueOf(rs.getInt("duration")));
                    break;
            }

            // Mostrar el panel de modificación
            int result = JOptionPane.showConfirmDialog(null, panel, "Modificar Material", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                // Preparar la actualización según el tipo de material
                String updateSQL = "";
                PreparedStatement pstmt = null;

                switch (tableFound) {
                    case "Books":
                        updateSQL = "UPDATE Books SET title = ?, author = ?, pages = ?, publisher = ?, isbn = ?, publication_year = ?, available_units = ? WHERE code = ?";
                        pstmt = conn.prepareStatement(updateSQL);
                        pstmt.setString(1, titleField.getText());
                        pstmt.setString(2, fieldList.get(0).getText());
                        pstmt.setInt(3, Integer.parseInt(fieldList.get(1).getText()));
                        pstmt.setString(4, fieldList.get(2).getText());
                        pstmt.setString(5, fieldList.get(3).getText());
                        pstmt.setInt(6, Integer.parseInt(fieldList.get(4).getText()));
                        pstmt.setInt(7, Integer.parseInt(fieldList.get(5).getText()));
                        pstmt.setString(8, code);
                        break;
                    case "Magazines":
                        updateSQL = "UPDATE Magazines SET title = ?, publisher = ?, periodicity = ?, publication_date = ?, available_units = ? WHERE code = ?";
                        pstmt = conn.prepareStatement(updateSQL);
                        pstmt.setString(1, titleField.getText());
                        pstmt.setString(2, fieldList.get(0).getText());
                        pstmt.setString(3, fieldList.get(1).getText());
                        pstmt.setString(4, fieldList.get(2).getText());
                        pstmt.setInt(5, Integer.parseInt(fieldList.get(3).getText()));
                        pstmt.setString(6, code);
                        break;
                    case "CDs":
                        updateSQL = "UPDATE CDs SET title = ?, artist = ?, genre = ?, duration = ?, track_count = ?, available_units = ? WHERE code = ?";
                        pstmt = conn.prepareStatement(updateSQL);
                        pstmt.setString(1, titleField.getText());
                        pstmt.setString(2, fieldList.get(0).getText());
                        pstmt.setString(3, fieldList.get(1).getText());
                        pstmt.setInt(4, Integer.parseInt(fieldList.get(2).getText()));
                        pstmt.setInt(5, Integer.parseInt(fieldList.get(3).getText()));
                        pstmt.setInt(6, Integer.parseInt(fieldList.get(4).getText()));
                        pstmt.setString(7, code);
                        break;
                    case "DVDs":
                        updateSQL = "UPDATE DVDs SET title = ?, director = ?, genre = ?, duration = ? WHERE code = ?";
                        pstmt = conn.prepareStatement(updateSQL);
                        pstmt.setString(1, titleField.getText());
                        pstmt.setString(2, fieldList.get(0).getText());
                        pstmt.setString(3, fieldList.get(1).getText());
                        pstmt.setInt(4, Integer.parseInt(fieldList.get(2).getText()));
                        pstmt.setString(5, code);
                        break;
                }

                // Actualizar la BD
                if (pstmt != null) {
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Material actualizado correctamente.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Función para los campos del form para modificar
    private void addModifyField(JPanel panel, GridBagConstraints gbc, ArrayList<JTextField> fieldList, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(label + ":"), gbc);
        gbc.gridx++;
        JTextField field = new JTextField(value, 20);
        panel.add(field, gbc);
        fieldList.add(field);
    }


    private void deleteMaterial() {
        String code = JOptionPane.showInputDialog(null, "Ingresa el código del material a borrar:", "Borrar Material", JOptionPane.QUESTION_MESSAGE);

        if (code == null || code.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Código es requerido.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String[] tables = {"Books", "Magazines", "CDs", "DVDs"};
            String tableFound = null;
            String title = null;

            // Buscar el material en las tablas
            for (String table : tables) {
                String searchSQL = "SELECT code, title FROM " + table + " WHERE code = ?";
                PreparedStatement pstmt = conn.prepareStatement(searchSQL);
                pstmt.setString(1, code);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    tableFound = table;
                    title = rs.getString("title"); // Traer el título del material
                    break;
                }
            }

            if (tableFound == null) {
                JOptionPane.showMessageDialog(null, "Material no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirmar borrado
            int confirmation = JOptionPane.showConfirmDialog(null, 
                "¿Estás seguro que desear eliminar el material '" + title + "' con código: " + code + "?", 
                "Confirmar borrado", JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                // Operación de borrado
                String deleteSQL = "DELETE FROM " + tableFound + " WHERE code = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL);
                deleteStmt.setString(1, code);

                int rowsAffected = deleteStmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Material '" + title + "' borrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Borrado fallido, vuelve a intentar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void searchMaterial() {
        String code = JOptionPane.showInputDialog(null, "Ingresa el código del material a buscar:", "Buscar material", JOptionPane.QUESTION_MESSAGE);

        if (code == null || code.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Código es requerido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String[] tables = {"Books", "Magazines", "CDs", "DVDs"};

            for (String table : tables) {
                String searchSQL = "SELECT * FROM " + table + " WHERE code = ?";
                PreparedStatement pstmt = conn.prepareStatement(searchSQL);
                pstmt.setString(1, code);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    StringBuilder result = new StringBuilder();
                    result.append("Código: ").append(rs.getString("code")).append("\n");
                    result.append("Título: ").append(rs.getString("title")).append("\n");

                    switch (table) {
                        case "Books":
                            result.append("Autor: ").append(rs.getString("author")).append("\n");
                            result.append("Páginas: ").append(rs.getInt("pages")).append("\n");
                            result.append("Editorial: ").append(rs.getString("publisher")).append("\n");
                            result.append("ISBN: ").append(rs.getString("isbn")).append("\n");
                            result.append("Año de publicación: ").append(rs.getInt("publication_year")).append("\n");
                            result.append("Unidades disponibles: ").append(rs.getInt("available_units")).append("\n");
                            break;
                        case "Magazines":
                            result.append("Editorial: ").append(rs.getString("publisher")).append("\n");
                            result.append("Periodicidad: ").append(rs.getString("periodicity")).append("\n");
                            result.append("Fecha de publicación: ").append(rs.getDate("publication_date")).append("\n");
                            result.append("Unidades disponibles: ").append(rs.getInt("available_units")).append("\n");
                            break;
                        case "CDs":
                            result.append("Artista: ").append(rs.getString("artist")).append("\n");
                            result.append("Género: ").append(rs.getString("genre")).append("\n");
                            result.append("Duración: ").append(rs.getInt("duration")).append(" minutes\n");
                            result.append("Número de canciones: ").append(rs.getInt("track_count")).append("\n");
                            result.append("Unidades disponibles: ").append(rs.getInt("available_units")).append("\n");
                            break;
                        case "DVDs":
                            result.append("Director: ").append(rs.getString("director")).append("\n");
                            result.append("Género: ").append(rs.getString("genre")).append("\n");
                            result.append("Duración: ").append(rs.getInt("duration")).append(" minutes\n");
                            break;
                    }

                    JOptionPane.showMessageDialog(null, result.toString(), "Material encontrado", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            JOptionPane.showMessageDialog(null, "Material no encontrado.", "Material", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listMaterials() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String[] columnNames = {"Código", "Título", "Tipo"};
            ArrayList<Object[]> data = new ArrayList<>();

            String[] tables = {"Books", "Magazines", "CDs", "DVDs"};

            // Buscar en las tablas y traer los materiales
            for (String table : tables) {
                String selectSQL = "SELECT code, title FROM " + table;
                PreparedStatement pstmt = conn.prepareStatement(selectSQL);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    String code = rs.getString("code");
                    String title = rs.getString("title");
                    data.add(new Object[]{code, title, table});
                }
            }

            // Convertir información en un array 2D
            Object[][] dataArray = data.toArray(new Object[0][]);
            
            // Crear tabla con la información
            JTable table = new JTable(dataArray, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            
            // Mostrar la tabla
            JOptionPane.showMessageDialog(null, scrollPane, "Lista de Materiales", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
