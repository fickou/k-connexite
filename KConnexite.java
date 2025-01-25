import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
        graph.ajouterArrete(1, 2);
        graph.ajouterArrete(1, 3);
        graph.ajouterArrete(1, 5);
        graph.ajouterArrete(2, 3);
        graph.ajouterArrete(1, 4);
        graph.ajouterArrete(3, 4);
        graph.ajouterArrete(3, 5);
        graph.ajouterArrete(4, 5);

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
                boolean result = graph.estKSommetConnexe(k);
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
                boolean result = graph.estKAreteConnexe(k);
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
    
            int centerX = 400; // Centre du cercle (axe X)
            int centerY = 300; // Centre du cercle (axe Y)
            int radius = 200;  // Rayon du cercle
            
            for (int i = 0; i < graph.V; i++) {
                double angle = 2 * Math.PI * i / graph.V; // Angle en radians
                positions[i][0] = (int) (centerX + radius * Math.cos(angle));
                positions[i][1] = (int) (centerY + radius * Math.sin(angle));
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
                    if (u < v) { // Éviter les doublons
                        g2d.drawLine(positions[u][0], positions[u][1], positions[v][0], positions[v][1]);
                    }
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
            if (u != v && !adj.get(u).contains(v)) { // Éviter les boucles et les doublons
                adj.get(u).add(v);
                adj.get(v).add(u);
            }
        }

        public boolean estKSommetConnexe(int k) {
            if (k > V) return false; // Impossible d'être k-sommet-connexe si k > V

            // Générer toutes les combinaisons de (k-1) sommets à supprimer
            List<List<Integer>> combinaisons = genererCombinaisons(V, k - 1);
            for (List<Integer> combinaison : combinaisons) {
                boolean[] sommetSupprime = new boolean[V];
                for (int sommet : combinaison) {
                    sommetSupprime[sommet] = true; // Marquer comme supprimé
                }
                if (!estConnexeApresSuppression(sommetSupprime)) return false;
            }
            return true;
        }

        public boolean estKAreteConnexe(int k) {
            // Générer toutes les combinaisons de (k) arêtes à supprimer
            List<int[]> arretes = new ArrayList<>();
            for (int u = 0; u < V; u++) {
                for (int v : adj.get(u)) {
                    if (u < v) arretes.add(new int[]{u, v}); // Éviter les doublons
                }
            }

            List<List<int[]>> combinaisons = genererCombinaisonsArretes(arretes, k);
            for (List<int[]> combinaison : combinaisons) {
                // Supprimer les arêtes de la combinaison
                for (int[] arrete : combinaison) {
                    suppArrete(arrete[0], arrete[1]);
                }

                // Vérifier si le graphe reste connexe
                boolean[] visite = new boolean[V];
                dfs(0, visite); // Commencer le parcours depuis un sommet arbitraire

                // Rétablir les arêtes supprimées
                for (int[] arrete : combinaison) {
                    ajouterArrete(arrete[0], arrete[1]);
                }

                // Vérifier si tous les sommets sont visités
                for (int i = 0; i < V; i++) {
                    if (!visite[i]) return false; // Le graphe n'est pas connexe
                }
            }
            return true;
        }

        private boolean estConnexeApresSuppression(boolean[] sommetSupprime) {
            boolean[] visite = new boolean[V];
            int start = -1;

            // Trouver un sommet non supprimé pour démarrer le DFS
            for (int i = 0; i < V; i++) {
                if (!sommetSupprime[i]) {
                    start = i;
                    break;
                }
            }
            if (start == -1) return true; // Tous les sommets sont supprimés

            dfs(start, visite, sommetSupprime);

            // Vérifier si tous les sommets non supprimés sont visités
            for (int i = 0; i < V; i++) {
                if (!sommetSupprime[i] && !visite[i]) {
                    return false; // Le graphe n'est pas connexe
                }
            }
            return true;
        }

        private void dfs(int v, boolean[] visite, boolean[] sommetSupprime) {
            visite[v] = true;
            for (int neighbor : adj.get(v)) {
                if (!sommetSupprime[neighbor] && !visite[neighbor]) {
                    dfs(neighbor, visite, sommetSupprime);
                }
            }
        }

        private void dfs(int v, boolean[] visite) {
            visite[v] = true;
            for (int neighbor : adj.get(v)) {
                if (!visite[neighbor]) {
                    dfs(neighbor, visite);
                }
            }
        }

        private void suppArrete(int u, int v) {
            adj.get(u).remove((Integer) v);
            adj.get(v).remove((Integer) u);
        }

        private List<List<Integer>> genererCombinaisons(int n, int k) {
            List<List<Integer>> result = new ArrayList<>();
            genererCombinaisonsRec(0, n, k, new ArrayList<>(), result);
            return result;
        }

        private void genererCombinaisonsRec(int start, int n, int k, List<Integer> current, List<List<Integer>> result) {
            if (k == 0) {
                result.add(new ArrayList<>(current));
                return;
            }
            for (int i = start; i < n; i++) {
                current.add(i);
                genererCombinaisonsRec(i + 1, n, k - 1, current, result);
                current.remove(current.size() - 1);
            }
        }

        private List<List<int[]>> genererCombinaisonsArretes(List<int[]> arretes, int k) {
            List<List<int[]>> result = new ArrayList<>();
            genererCombinaisonsArretesRec(0, arretes, k, new ArrayList<>(), result);
            return result;
        }

        private void genererCombinaisonsArretesRec(int start, List<int[]> arretes, int k, List<int[]> current, List<List<int[]>> result) {
            if (k == 0) {
                result.add(new ArrayList<>(current));
                return;
            }
            for (int i = start; i < arretes.size(); i++) {
                current.add(arretes.get(i));
                genererCombinaisonsArretesRec(i + 1, arretes, k - 1, current, result);
                current.remove(current.size() - 1);
            }
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