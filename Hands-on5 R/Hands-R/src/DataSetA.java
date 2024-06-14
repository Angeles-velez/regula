import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class DataSetA extends Agent {
    private AID agenteClasificacion;
    private String tecnicaAnalisis;
    private String tecnicaOptimizacion;
    private double[] datosX = {23, 26, 30, 34, 43, 48, 52, 57, 58};
    private double[] datosY = {651, 762, 856, 1063, 1190, 1298, 1421, 1440, 1518};

    protected void setup() {
        System.out.println("Agente con Conjunto de Datos " + getLocalName() + " iniciado.");

        SequentialBehaviour secuencial = new SequentialBehaviour();

        System.out.println("Buscando Clasificación...");
        secuencial.addSubBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                if (agenteClasificacion == null) {
                    agenteClasificacion = localizarAgente("classification-service");
                    if (agenteClasificacion != null) {
                        System.out.println("Agente de Clasificación encontrado: " + agenteClasificacion.getName());
                        enviarConjuntoDatos(agenteClasificacion, datosX, datosY);
                    } else {
                        System.out.println("Agente de Clasificación no encontrado.");
                    }
                }
            }
        });

        secuencial.addSubBehaviour(new RecibirRespuestaClasificacion());

        secuencial.addSubBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                System.out.println("Segundo Ticket");
                if (tecnicaAnalisis != null && tecnicaOptimizacion != null) {
                    AID agenteAnalisis = localizarAgente(tecnicaAnalisis);
                    if (agenteAnalisis != null) {
                        System.out.println("Agente de Análisis encontrado: " + agenteAnalisis.getName());
                        enviarDatosAAgente(agenteAnalisis, tecnicaOptimizacion, datosX, datosY);
                        tecnicaAnalisis = null;
                        tecnicaOptimizacion = null;
                        System.out.println("Envío de Datos a Agente de Análisis");
                    } else {
                        System.out.println("Agente de Análisis para " + tecnicaAnalisis + " no encontrado.");
                    }

                    AID agenteOptimizacion = localizarAgente(tecnicaOptimizacion);
                    if (agenteOptimizacion != null) {
                        System.out.println("Agente de Optimización encontrado: " + agenteOptimizacion.getName());
                    } else {
                        System.out.println("Agente de Optimización para " + tecnicaOptimizacion + " no encontrado.");
                    }
                }
            }
        });

        addBehaviour(secuencial);
    }

    protected void takeDown() {
        System.out.println("AgenteConjuntoDatos " + getAID().getName() + " finalizando.");
    }

    private AID localizarAgente(String tipoServicio) {
        AID agente = null;
        DFAgentDescription plantilla = new DFAgentDescription();
        ServiceDescription descripcionServicio = new ServiceDescription();
        descripcionServicio.setType(tipoServicio);
        plantilla.addServices(descripcionServicio);
        try {
            DFAgentDescription[] resultado = DFService.search(this, plantilla);
            if (resultado.length > 0) {
                agente = resultado[0].getName();
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        return agente;
    }

    private void enviarConjuntoDatos(AID receptor, double[] x, double[] y) {
        System.out.println("Enviando Conjunto de Datos...");
        ACLMessage mensaje = new ACLMessage(ACLMessage.REQUEST);
        mensaje.addReceiver(receptor);
        mensaje.setConversationId("dataset-classification");
        mensaje.setReplyWith("request" + System.currentTimeMillis());

        StringBuilder sb = new StringBuilder();
        sb.append("x:");
        for (double val : x) sb.append(val).append(",");
        sb.append(";y:");
        for (double val : y) sb.append(val).append(",");
        mensaje.setContent(sb.toString());

        send(mensaje);
    }

    private void enviarDatosAAgente(AID agenteAID, String tecnicaOptimizacion, double[] x, double[] y) {
        System.out.println("Enviando Datos al Agente");
        ACLMessage mensaje = new ACLMessage(ACLMessage.REQUEST);
        mensaje.addReceiver(agenteAID);
        mensaje.setConversationId("analysis-optimization");
        System.out.println("Hola desde AgenteConjuntoDatos");

        StringBuilder sb = new StringBuilder();
        sb.append("x:");
        for (double val : x) sb.append(val).append(",");
        sb.append(";y:");
        for (double val : y) sb.append(val).append(",");
        sb.append(";optim:").append(tecnicaOptimizacion);
        mensaje.setContent(sb.toString());

        send(mensaje);
    }

    private class RecibirRespuestaClasificacion extends Behaviour {

        private MessageTemplate plantilla;
        private boolean completado = false;

        public void action() {
            System.out.println("Recibiendo Respuesta de Clasificación");
            plantilla = MessageTemplate.MatchConversationId("dataset-classification");
            ACLMessage mensaje = myAgent.receive(plantilla);
            if (mensaje != null) {
                if (mensaje.getPerformative() == ACLMessage.INFORM) {
                    String respuesta = mensaje.getContent();

                    System.out.println("Respuesta de Clasificación recibida: " + respuesta);
                    String[] partes = respuesta.split(",");
                    tecnicaAnalisis = partes[0];
                    tecnicaOptimizacion = partes[1];
                }
                completado = true;
            } else {
                block();
            }
        }

        public boolean done() {
            return completado;
        }
    }
}
