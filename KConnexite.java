import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class KConnexite extends JFrame {
    private Graph graph;
    private int k; // Valeur de k pour la k-connexité

    public KConnexite() {
        setTitle("Visualisation du Graphe");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialiser le graphe
        graph = new Graph(6);
        graph.ajouterArrete(0, 1);
        graph.ajouterArrete(0, 2);
        graph.ajouterArrete(0, 3);
        graph.ajouterArrete(1, 1);
        graph.ajouterArrete(1, 2);
        graph.ajouterArrete(1, 3);
        graph.ajouterArrete(1, 5);
        graph.ajouterArrete(2, 3);
        graph.ajouterArrete(1, 4);
        graph.ajouterArrete(3, 4);
        graph.ajouterArrete(3, 5);
        graph.ajouterArrete(4, 5);
        graph.ajouterArrete(5, 3);
        graph.ajouterArrete(5, 4);

        k = 2; // Valeur par défaut de k

        // Panneau pour afficher le graphe
        GraphPanel graphPanel = new GraphPanel(graph);
        add(graphPanel, BorderLayout.CENTER);

        // Panneau pour les contrôles
        JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout());

        JLabel kLabel = new JLabel("k : ");
        JTextField kField = new JTextField(String.valueOf(k), 3);
        JButton testerSommetConnexe = new JButton("Tester k-sommet-connexite");
        JButton testerArretConnexe = new JButton("Tester k-arete-connexite");

        controls.add(kLabel);
        controls.add(kField);
        controls.add(testerSommetConnexe);
        controls.add(testerArretConnexe);

        add(controls, BorderLayout.SOUTH);

        // Action pour tester la k-sommet-connexité
        testerSommetConnexe.addActionListener(e -> {
            try {
                k = Integer.parseInt(kField.getText());
                boolean result = graph.estSommetConnexe(k);
                JOptionPane.showMessageDialog(this, "Le graphe est " + (result ? "" : "non ") + k + "-sommet-connexe.");
                graphPanel.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer une valeur valide pour k.");
            }
        });

        // Action pour tester la k-arête-connexité
        testerArretConnexe.addActionListener(e -> {
            try {
                k = Integer.parseInt(kField.getText());
                boolean result = graph.estArretConnexe(k);
                JOptionPane.showMessageDialog(this, "Le graphe est " + (result ? "" : "non ") + k + "-arête-connexe.");
                graphPanel.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer une valeur valide pour k.");
            }
        });
    }

    // Classe pour dessiner le graphe
    class GraphPanel extends JPanel {
        private Graph graph;
        private int[][] positions; // Positions des sommets pour l'affichage

        public GraphPanel(Graph graph) {
            this.graph = graph;
            positions = new int[graph.V][2];

            // Générer des positions aléatoires pour les sommets
            Random rand = new Random();
            for (int i = 0; i < graph.V; i++) {
                positions[i][0] = rand.nextInt(600) + 50;
                positions[i][1] = rand.nextInt(400) + 50;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Dessiner les arêtes
            g2d.setColor(Color.BLACK);
            for (int u = 0; u < graph.V; u++) {
                for (int v : graph.adj.get(u)) {
                    g2d.drawLine(positions[u][0], positions[u][1], positions[v][0], positions[v][1]);
                }
            }

            // Dessiner les sommets
            for (int i = 0; i < graph.V; i++) {
                g2d.setColor(Color.BLUE);
                g2d.fillOval(positions[i][0] - 10, positions[i][1] - 10, 20, 20);
                g2d.setColor(Color.WHITE);
                g2d.drawString(String.valueOf(i), positions[i][0] - 5, positions[i][1] + 5);
            }
        }
    }

    // Classe pour représenter un graphe
    static class Graph {
        private int V; // Nombre de sommets
        private List<List<Integer>> adj; // Liste d'adjacence

        public Graph(int V) {
            this.V = V;
            adj = new ArrayList<>();
            for (int i = 0; i < V; i++) {
                adj.add(new ArrayList<>());
            }
        }

        public void ajouterArrete(int u, int v) {
            adj.get(u).add(v);
            adj.get(v).add(u);
        }

        public boolean estSommetConnexe(int k) {
            for (int i = 0; i < V; i++) {
                boolean[] visited = new boolean[V];
                visited[i] = true;
                if (!estConnexeApresSupp(visited)) return false;
            }
            return true;
        }

        public boolean estArretConnexe(int k) {
            for (int u = 0; u < V; u++) {
                for (int v : adj.get(u)) {
                    suppArrete(u, v);
                    boolean[] visited = new boolean[V];
                    if (!estConnexeApresSupp(visited)) {
                        ajouterArrete(u, v);
                        return false;
                    }
                    ajouterArrete(u, v);
                }
            }
            return true;
        }

        private boolean estConnexeApresSupp(boolean[] visited) {
            int start = -1;
            for (int i = 0; i < V; i++) {
                if (!visited[i]) {
                    start = i;
                    break;
                }
            }
            if (start == -1) return true;
            dfs(start, visited);
            for (int i = 0; i < V; i++) {
                if (!visited[i]) return false;
            }
            return true;
        }

        private void dfs(int v, boolean[] visited) {
            visited[v] = true;
            for (int neighbor : adj.get(v)) {
                if (!visited[neighbor]) dfs(neighbor, visited);
            }
        }

        private void suppArrete(int u, int v) {
            adj.get(u).remove((Integer) v);
            adj.get(v).remove((Integer) u);
        }
    }

    // Méthode principale
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KConnexite frame = new KConnexite();
            frame.setVisible(true);
        });
    }
}
