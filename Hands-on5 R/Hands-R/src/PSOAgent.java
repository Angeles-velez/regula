import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.Arrays;
import java.util.Random;

public class AgentPSO extends Agent {

    protected void setup() {
        System.out.println("Agent " + getLocalName() + " started.");

        // Register the PSO service with the Directory Facilitator
        registerService("PSOAgent", "pso");

        // Add behavior to receive the dataset from DataSetAgent
        addBehaviour(new ReceiveDataSetBehavior());
    }

    protected void takeDown() {
        // Deregister the service when agent is terminated
        deregisterService();
        System.out.println("Agent " + getLocalName() + " terminated.");
    }

    private void registerService(String agentName, String serviceType) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceType);
        sd.setName(agentName);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            System.out.println("Service registered: " + agentName + " - " + serviceType);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private void deregisterService() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private class ReceiveDataSetBehavior extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String response = msg.getContent();
                String[] parts = response.split(";");

                // Extract dataset values
                String[] xValues = parts[0].replace("x:", "").split(",");
                String[] yValues = parts[1].replace("y:", "").split(",");
                String optimizationTechnique = parts[2].replace("optim:", "");

                // Convert data to appropriate format for PSO
                double[] xData = Arrays.stream(xValues).mapToDouble(Double::parseDouble).toArray();
                double[] yData = Arrays.stream(yValues).mapToDouble(Double::parseDouble).toArray();

                if (optimizationTechnique.equals("pso")) {
                    myAgent.addBehaviour(new PSOBehaviour(xData, yData));
                }
            } else {
                block();
            }
        }
    }

    private class PSOBehaviour extends Behaviour {
        private double[] x;
        private double[] y;
        private static final int NUM_PARTICLES = 50;
        private static final int MAX_ITERATIONS = 100;
        private Particle[] particles;
        private Particle gBest;
        private int iteration;

        public PSOBehaviour(double[] x, double[] y) {
            this.x = x;
            this.y = y;
            this.particles = new Particle[NUM_PARTICLES];
            this.iteration = 0;
            initializeParticles();
        }

        private void initializeParticles() {
            for (int i = 0; i < NUM_PARTICLES; i++) {
                particles[i] = new Particle();
            }
            gBest = particles[0];
            for (Particle particle : particles) {
                if (calculateFitness(particle) < calculateFitness(gBest)) {
                    gBest = particle;
                }
            }
        }

        private double calculateFitness(Particle particle) {
            double B0 = particle.getB0();
            double B1 = particle.getB1();
            double error = 0.0;
            for (int i = 0; i < x.length; i++) {
                double predictedY = B0 + B1 * x[i];
                error += Math.pow(y[i] - predictedY, 2);
            }
            return error;
        }

        private void updateParticles() {
            Random rand = new Random();
            for (Particle particle : particles) {
                for (int i = 0; i < particle.getVelocity().length; i++) {
                    double inertia = particle.getVelocity()[i];
                    double cognitive = rand.nextDouble() * (particle.getpBest()[i] - particle.getPosition()[i]);
                    double social = rand.nextDouble() * (gBest.getPosition()[i] - particle.getPosition()[i]);
                    particle.getVelocity()[i] = inertia + cognitive + social;
                    particle.getPosition()[i] += particle.getVelocity()[i];
                }
                if (calculateFitness(particle) < calculateFitness(particle.getpBestParticle())) {
                    particle.setpBest(particle.getPosition());
                    if (calculateFitness(particle) < calculateFitness(gBest)) {
                        gBest = particle;
                    }
                }
            }
        }

        public void action() {
            if (iteration < MAX_ITERATIONS) {
                updateParticles();
                iteration++;
                System.out.println("Iteration " + iteration + ": Best Fitness = " + calculateFitness(gBest) +
                        ", B0 = " + gBest.getB0() + ", B1 = " + gBest.getB1());
            } else {
                System.out.println("\nBest solution found: B0 = " + gBest.getB0() + ", B1 = " + gBest.getB1() +
                        ", Fitness = " + calculateFitness(gBest));
                System.out.println("Regression Equation: y = " + gBest.getB0() + " + " + gBest.getB1() + "x");
                myAgent.doDelete();
            }
        }

        public boolean done() {
            return iteration >= MAX_ITERATIONS;
        }

        private class Particle {
            private double[] position;
            private double[] velocity;
            private double[] pBest;

            public Particle() {
                Random rand = new Random();
                this.position = new double[]{rand.nextDouble() * 1000, rand.nextDouble() * 1000};
                this.velocity = new double[]{rand.nextDouble(), rand.nextDouble()};
                this.pBest = this.position.clone();
            }

            public double[] getPosition() {
                return position;
            }

            public double[] getVelocity() {
                return velocity;
            }

            public double[] getpBest() {
                return pBest;
            }

            public void setpBest(double[] pBest) {
                this.pBest = pBest;
            }

            public double getB0() {
                return position[0];
            }

            public double getB1() {
                return position[1];
            }

            public Particle getpBestParticle() {
                Particle pBestParticle = new Particle();
                pBestParticle.position = this.pBest;
                return pBestParticle;
            }
        }
    }
}
