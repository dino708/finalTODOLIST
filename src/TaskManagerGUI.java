import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TaskManagerGUI extends JFrame {
    private DaftarTugas daftarTugas;
    private Laporan laporan;

    private DefaultListModel<Task> listModel;
    private JList<Task> taskList;
    private JTextField txtJudul, txtDeadline;
    private JTextArea txtDeskripsi;
    private JComboBox<String> cmbJenis, cmbFilter;
    private JCheckBox chkSelesai;
    private JLabel lblStatus;

    private Task selectedTask = null;

    public TaskManagerGUI() {
        daftarTugas = new DaftarTugas();
        laporan = new Laporan(daftarTugas);

        initComponents();
        loadTasks();
    }

    private void initComponents() {
        setTitle("📋 Aplikasi To-Do List / Task Manager");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 244, 248));

        // Header
        JLabel header = new JLabel("📋 Task Manager", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(new Color(44, 62, 80));
        header.setBorder(new EmptyBorder(0, 0, 15, 0));
        mainPanel.add(header, BorderLayout.NORTH);

        // Center Panel - Split into List and Form
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450);

        // Left Panel - Task List
        JPanel leftPanel = createListPanel();
        splitPane.setLeftComponent(leftPanel);

        // Right Panel - Form
        JPanel rightPanel = createFormPanel();
        splitPane.setRightComponent(rightPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Status Bar
        lblStatus = new JLabel("Siap");
        lblStatus.setBorder(new EmptyBorder(5, 5, 5, 5));
        mainPanel.add(lblStatus, BorderLayout.SOUTH);

        add(mainPanel);

        // Window Listener untuk menutup database
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                daftarTugas.closeDatabase();
            }
        });
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                "Daftar Tugas", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(52, 152, 219)
        ));
        panel.setBackground(Color.WHITE);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(new JLabel("Filter: "));
        cmbFilter = new JComboBox<>(new String[]{"Semua", "Selesai", "Belum Selesai",
                "Pekerjaan", "Kuliah", "Pribadi"});
        cmbFilter.addActionListener(e -> filterTasks());
        filterPanel.add(cmbFilter);
        panel.add(filterPanel, BorderLayout.NORTH);

        // Task List
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setCellRenderer(new TaskCellRenderer());
        taskList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedTask = taskList.getSelectedValue();
                if (selectedTask != null) {
                    populateForm(selectedTask);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskList);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
                "Detail Tugas", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(46, 204, 113)
        ));
        panel.setBackground(Color.WHITE);

        // Form Fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Judul
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Judul:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtJudul = new JTextField(20);
        formPanel.add(txtJudul, gbc);

        // Jenis
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Jenis:"), gbc);
        gbc.gridx = 1;
        cmbJenis = new JComboBox<>(new String[]{"Pekerjaan", "Kuliah", "Pribadi"});
        formPanel.add(cmbJenis, gbc);

        // Deadline
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Deadline (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        txtDeadline = new JTextField(20);
        txtDeadline.setText(LocalDate.now().plusDays(7).toString());
        formPanel.add(txtDeadline, gbc);

        // Deskripsi
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Deskripsi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        txtDeskripsi = new JTextArea(5, 20);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDeskripsi);
        formPanel.add(descScroll, gbc);

        // Status Checkbox
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        chkSelesai = new JCheckBox("Tandai sebagai Selesai");
        chkSelesai.setBackground(Color.WHITE);
        chkSelesai.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(chkSelesai, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 5, 5, 5));

        JButton btnTambah = createStyledButton("➕ Tambah", new Color(46, 204, 113));
        JButton btnUpdate = createStyledButton("✏️ Update", new Color(52, 152, 219));
        JButton btnHapus = createStyledButton("🗑️ Hapus", new Color(231, 76, 60));
        JButton btnClear = createStyledButton("🔄 Clear", new Color(149, 165, 166));
        JButton btnLaporan = createStyledButton("📊 Laporan", new Color(155, 89, 182));
        JButton btnToggle = createStyledButton("✓ Tandai selesai", new Color(241, 196, 15));

        btnTambah.addActionListener(e -> tambahTask());
        btnUpdate.addActionListener(e -> updateTask());
        btnHapus.addActionListener(e -> hapusTask());
        btnClear.addActionListener(e -> clearForm());
        btnLaporan.addActionListener(e -> showLaporan());
        btnToggle.addActionListener(e -> toggleStatus());

        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnLaporan);
        buttonPanel.add(btnToggle);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK    );
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadTasks() {
        listModel.clear();
        List<Task> tasks = daftarTugas.getSemuaTugas();
        for (Task task : tasks) {
            listModel.addElement(task);
        }
        updateStatus("Loaded " + tasks.size() + " tugas");
    }

    private void filterTasks() {
        listModel.clear();
        String filter = (String) cmbFilter.getSelectedItem();
        List<Task> tasks;

        tasks = switch (filter) {
            case "Selesai" -> daftarTugas.filterByStatus(true);
            case "Belum Selesai" -> daftarTugas.filterByStatus(false);
            case "Pekerjaan", "Kuliah", "Pribadi" -> daftarTugas.filterByJenis(filter);
            default -> daftarTugas.getSemuaTugas();
        };

        for (Task task : tasks) {
            listModel.addElement(task);
        }
        updateStatus("Filter: " + filter + " (" + tasks.size() + " tugas)");
    }

    private void tambahTask() {
        if (!validateForm()) return;

        try {
            String judul = txtJudul.getText().trim();
            String deskripsi = txtDeskripsi.getText().trim();
            LocalDate deadline = LocalDate.parse(txtDeadline.getText().trim());
            String jenis = (String) cmbJenis.getSelectedItem();

            Task task = switch (jenis) {
                case "Pekerjaan" -> new TugasPekerjaan(judul, deskripsi, deadline);
                case "Kuliah" -> new TugasKuliah(judul, deskripsi, deadline);
                default -> new TugasPribadi(judul, deskripsi, deadline);
            };

            daftarTugas.tambah(task);
            loadTasks();
            clearForm();
            updateStatus("Tugas berhasil ditambahkan!");
            JOptionPane.showMessageDialog(this, "Tugas berhasil ditambahkan!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Format tanggal tidak valid! Gunakan yyyy-MM-dd",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTask() {
        if (selectedTask == null) {
            JOptionPane.showMessageDialog(this, "Pilih tugas yang ingin diupdate!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm()) return;

        try {
            selectedTask.setJudul(txtJudul.getText().trim());
            selectedTask.setDeskripsi(txtDeskripsi.getText().trim());
            selectedTask.setDeadline(LocalDate.parse(txtDeadline.getText().trim()));
            selectedTask.setSelesai(chkSelesai.isSelected());

            daftarTugas.update(selectedTask);
            loadTasks();
            clearForm();
            updateStatus("Tugas berhasil diupdate!");
            JOptionPane.showMessageDialog(this, "Tugas berhasil diupdate!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Format tanggal tidak valid! Gunakan yyyy-MM-dd",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusTask() {
        if (selectedTask == null) {
            JOptionPane.showMessageDialog(this, "Pilih tugas yang ingin dihapus!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus tugas ini?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            daftarTugas.hapus(selectedTask.getId());
            loadTasks();
            clearForm();
            updateStatus("Tugas berhasil dihapus!");
        }
    }

    private void toggleStatus() {
        if (selectedTask == null) {
            JOptionPane.showMessageDialog(this, "Pilih tugas terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean newStatus = !selectedTask.isSelesai();
        daftarTugas.updateStatus(selectedTask.getId(), newStatus);
        loadTasks();
        updateStatus("Status tugas diubah menjadi: " + (newStatus ? "Selesai" : "Belum Selesai"));
    }

    private void showLaporan() {
        String laporanText = laporan.generateLaporan();

        JTextArea textArea = new JTextArea(laporanText);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 350));

        JOptionPane.showMessageDialog(this, scrollPane, "Laporan Tugas",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void populateForm(Task task) {
        txtJudul.setText(task.getJudul());
        txtDeskripsi.setText(task.getDeskripsi());
        txtDeadline.setText(task.getDeadline().toString());
        cmbJenis.setSelectedItem(task.getJenis());
        chkSelesai.setSelected(task.isSelesai());
    }

    private void clearForm() {
        txtJudul.setText("");
        txtDeskripsi.setText("");
        txtDeadline.setText(LocalDate.now().plusDays(7).toString());
        cmbJenis.setSelectedIndex(0);
        chkSelesai.setSelected(false);
        selectedTask = null;
        taskList.clearSelection();
    }

    private boolean validateForm() {
        if (txtJudul.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Judul tidak boleh kosong!",
                    "Validasi Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtDeadline.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Deadline tidak boleh kosong!",
                    "Validasi Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void updateStatus(String message) {
        lblStatus.setText("Status: " + message);
    }

    // Custom Cell Renderer untuk Task List
    private class TaskCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Task task) {
                setText(task.toString());

                if (task.isSelesai()) {
                    setForeground(isSelected ? Color.WHITE : new Color(46, 204, 113));
                } else if (task.getDeadline().isBefore(LocalDate.now())) {
                    setForeground(isSelected ? Color.WHITE : new Color(231, 76, 60));
                }
            }

            setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));

            return this;
        }
    }
}