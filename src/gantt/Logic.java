package gantt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.*;
import javax.swing.*;

public class Logic {
    HashMap<Integer, int[]> processes = new HashMap<>(); // [burst time, arrival time]
    static public int globalY = 50; // Static for consistent placement
    public int processindex = 1;
    public int dataindex = 0;
    public int globalX = 3;

    // Add new process with burst time and arrival time
    public void add_new(int burst, int arrival) {
        processes.put(processindex, new int[]{burst, arrival});
        processindex++;
    }

    // Show process data in the table
    public void show_data(JTable table) {
        int[] process_data = processes.get(dataindex + 1);
        table.setValueAt(dataindex + 1, dataindex, 0);
        table.setValueAt(process_data[0], dataindex, 1); // Burst Time
        table.setValueAt(process_data[1], dataindex, 2); // Arrival Time
        dataindex++;
    }

    // Compute SJF Preemptive Scheduling
     public void compute_sjfpre(JTable table) {
        List<int[]> process_list = new ArrayList<>();
        for (Map.Entry<Integer, int[]> entry : processes.entrySet()) {
            int[] pData = entry.getValue();
            process_list.add(new int[]{entry.getKey(), pData[0], pData[1], 0, 0}); 
            // [ID, Burst Time, Arrival Time, Waiting Time, Turnaround Time]
        }

        // Sort by arrival time initially
        process_list.sort(Comparator.comparingInt(a -> a[2]));

        int currentTime = 0;
        int completed = 0;
        int totalProcesses = process_list.size();
        int[] remainingBurst = new int[totalProcesses];

        for (int i = 0; i < totalProcesses; i++) {
            remainingBurst[i] = process_list.get(i)[1]; // Initialize remaining burst times
        }

        while (completed < totalProcesses) {
            int shortestIndex = -1;
            int shortestTime = Integer.MAX_VALUE;

            // Find the process with the shortest remaining time
            for (int i = 0; i < totalProcesses; i++) {
                if (process_list.get(i)[2] <= currentTime && remainingBurst[i] > 0) {
                    if (remainingBurst[i] < shortestTime) {
                        shortestTime = remainingBurst[i];
                        shortestIndex = i;
                    }
                }
            }

            // If no process is available, move to the next arrival time
            if (shortestIndex == -1) {
                currentTime++;
                continue;
            }

            // Process the selected job for 1 time unit
            remainingBurst[shortestIndex]--;
            currentTime++;

            // Check if process is completed
            if (remainingBurst[shortestIndex] == 0) {
                completed++;
                int completionTime = currentTime;
                int arrivalTime = process_list.get(shortestIndex)[2];
                int burstTime = process_list.get(shortestIndex)[1];

                process_list.get(shortestIndex)[4] = completionTime - arrivalTime; // Turnaround Time
                process_list.get(shortestIndex)[3] = process_list.get(shortestIndex)[4] - burstTime; // Waiting Time
            }
        }

        // Update the table with results
        for (int i = 0; i < process_list.size(); i++) {
            int[] process = process_list.get(i);
            table.setValueAt(process[0], i, 0); // Process ID
            table.setValueAt(process[1], i, 1); // Burst Time
            table.setValueAt(process[2], i, 2); // Arrival Time
            table.setValueAt(process[3], i, 3); // Waiting Time
            table.setValueAt(process[4], i, 4); // Turnaround Time
        }
    }
    
    
    // Compute SJF Non-Preemptive Scheduling
public void computeSJFNonPreemptive(JTable table) {
    List<int[]> processList = new ArrayList<>();
    for (Map.Entry<Integer, int[]> entry : processes.entrySet()) {
        int[] pData = entry.getValue();
        processList.add(new int[]{entry.getKey(), pData[0], pData[1], 0, 0}); 
        // [ID, Burst Time, Arrival Time, Waiting Time, Turnaround Time]
    }

    // Sort by arrival time initially
    processList.sort(Comparator.comparingInt(a -> a[2]));

    int currentTime = 0;
    int completed = 0;
    int totalProcesses = processList.size();
    int[] remainingBurst = new int[totalProcesses];

    for (int i = 0; i < totalProcesses; i++) {
        remainingBurst[i] = processList.get(i)[1]; // Initialize burst times
    }

    while (completed < totalProcesses) {
        int shortestIndex = -1;
        int shortestTime = Integer.MAX_VALUE;

        // Find the process with the shortest burst time that has arrived
        for (int i = 0; i < totalProcesses; i++) {
            if (processList.get(i)[2] <= currentTime && remainingBurst[i] > 0) {
                if (remainingBurst[i] < shortestTime) {
                    shortestTime = remainingBurst[i];
                    shortestIndex = i;
                }
            }
        }

        // If no process is ready, move to the next arrival time
        if (shortestIndex == -1) {
            currentTime++;
            continue;
        }

        // Execute the selected process completely (non-preemptive)
        currentTime += remainingBurst[shortestIndex];
        int completionTime = currentTime;
        int arrivalTime = processList.get(shortestIndex)[2];
        int burstTime = processList.get(shortestIndex)[1];

        // Update turnaround and waiting times
        processList.get(shortestIndex)[4] = completionTime - arrivalTime; // Turnaround Time
        processList.get(shortestIndex)[3] = processList.get(shortestIndex)[4] - burstTime; // Waiting Time

        // Mark this process as completed
        remainingBurst[shortestIndex] = 0;
        completed++;
    }

    // Update the table with results
    for (int i = 0; i < processList.size(); i++) {
        int[] process = processList.get(i);
        table.setValueAt(process[0], i, 0); // Process ID
        table.setValueAt(process[1], i, 1); // Burst Time
        table.setValueAt(process[2], i, 2); // Arrival Time
        table.setValueAt(process[3], i, 3); // Waiting Time
        table.setValueAt(process[4], i, 4); // Turnaround Time
    }
}

    public double calculateAvgTurnaroundTime(JTable table) {
    int rowCount = processes.size();
    double totalTurnaroundTime = 0;
    
    for (int i = 0; i < rowCount; i++) {
        Object value = table.getValueAt(i, 4); // Column index 4 contains turnaround time
        if (value != null) {
            totalTurnaroundTime += Integer.parseInt(value.toString());
        }
    }
    
    return totalTurnaroundTime / rowCount;
}
    
    public double calculateAvgWaitingTime(JTable table) {
    int rowCount = processes.size();
    double totalWaitingTime = 0;
    
    for (int i = 0; i < rowCount; i++) {
        Object value = table.getValueAt(i, 3); // Column index 3 contains waiting time
        if (value != null) {
            totalWaitingTime += Integer.parseInt(value.toString());
        }
    }
    
    return totalWaitingTime / rowCount;
}
    // Generate Gantt Chart for SJF Preemptive
   public void creationSJF(JPanel table) {
    table.removeAll();   // Clear previous Gantt chart components
    table.setLayout(null); // Set absolute layout for positioning components
    globalX = 0;            // Reset horizontal starting position
    globalY = 0;            // Reset vertical position

    // Convert processes to a list for easier manipulation
    List<int[]> processList = new ArrayList<>();
    for (Map.Entry<Integer, int[]> entry : processes.entrySet()) {
        int[] processData = entry.getValue();
        processList.add(new int[]{entry.getKey(), processData[0], processData[1]}); 
        // [ID, Burst Time, Arrival Time]
    }

    // Sort processes by arrival time
    processList.sort(Comparator.comparingInt(a -> a[2]));

    int currentTime = 0; // Keeps track of the current execution time
    int totalBurstTime = processList.stream().mapToInt(p -> p[1]).sum(); // Total burst time of all processes
    int[] remainingBurst = processList.stream().mapToInt(p -> p[1]).toArray(); // Remaining burst times

    int processCount = processList.size();

    int previousProcessId = -1; // Track previous process ID for block merging
    int blockStartTime = 0;     // Start time for the current block

    while (currentTime < totalBurstTime) {
        int shortestIndex = -1;
        int shortestTime = Integer.MAX_VALUE;

        // Find process with shortest remaining burst time that has arrived
        for (int i = 0; i < processCount; i++) {
            if (processList.get(i)[2] <= currentTime && remainingBurst[i] > 0) {
                if (remainingBurst[i] < shortestTime) {
                    shortestTime = remainingBurst[i];
                    shortestIndex = i;
                }
            }
        }

        // If no process is ready, increment the current time
        if (shortestIndex == -1) {
            currentTime++;
            continue;
        }

        int processId = processList.get(shortestIndex)[0];

        // If process changes, finalize the previous block
        if (processId != previousProcessId && previousProcessId != -1) {
            addGanttBlock(table, previousProcessId, blockStartTime, currentTime, totalBurstTime);
            blockStartTime = currentTime; // Reset block start time
        }

        // Update remaining burst time
        remainingBurst[shortestIndex]--;
        currentTime++;
        previousProcessId = processId;
    }

    // Add the final block for the last process
    if (previousProcessId != -1) {
        addGanttBlock(table, previousProcessId, blockStartTime, currentTime, totalBurstTime);
    }

    // Final refresh to display Gantt chart
    table.revalidate();
    table.repaint();
}

/**
 * Helper method to add a Gantt block to the panel
 */
private void addGanttBlock(JPanel table, int processId, int startTime, int endTime, int totalBurstTime) {
    // Block width proportional to time executed
    double unitWidth = (double) (table.getWidth() - 8) / totalBurstTime;
    int blockWidth = (int) Math.ceil(unitWidth * (endTime - startTime));

    // Create process block
    JLabel processLabel = new JLabel("P" + processId);
    JPanel panel = new JPanel();
    panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    panel.setLayout(new BorderLayout());
    panel.add(processLabel, BorderLayout.CENTER);

    // Add the panel to Gantt chart
    table.add(panel);
    panel.setBounds(globalX, globalY, blockWidth, 25);

    // Add time marker at the end of the block
    JLabel timeLabel = new JLabel(Integer.toString(endTime));
    table.add(timeLabel);
    timeLabel.setBounds(globalX + blockWidth - 18, globalY + 25, 30, 20);

    // Update globalX for the next block
    globalX += blockWidth;
}

    public void creationSJFNonPreemptive(JPanel table) {
        table.removeAll();   // Clear previous Gantt chart components
        table.setLayout(null); // Set absolute layout for positioning components
        globalX = 0;            // Reset horizontal starting position
        globalY = 0;            // Reset vertical position

        // Convert processes to a list for easier manipulation
        List<int[]> processList = new ArrayList<>();
        for (Map.Entry<Integer, int[]> entry : processes.entrySet()) {
            int[] processData = entry.getValue();
            processList.add(new int[]{entry.getKey(), processData[0], processData[1]}); 
            // [ID, Burst Time, Arrival Time]
        }

        // Sort processes by arrival time initially
        processList.sort(Comparator.comparingInt(a -> a[2]));

        int currentTime = 0; // Keeps track of the current execution time
        int totalBurstTime = processList.stream().mapToInt(p -> p[1]).sum(); // Total burst time of all processes
        int processCount = processList.size();

        int previousProcessId = -1; // Track previous process ID for block merging
        int blockStartTime = 0;     // Start time for the current block

        while (processList.stream().anyMatch(p -> p[1] > 0)) {
            int shortestIndex = -1;
            int shortestTime = Integer.MAX_VALUE;

            // Find the process with the shortest burst time that has arrived
            for (int i = 0; i < processCount; i++) {
                if (processList.get(i)[2] <= currentTime && processList.get(i)[1] > 0) {
                    if (processList.get(i)[1] < shortestTime) {
                        shortestTime = processList.get(i)[1];
                        shortestIndex = i;
                    }
                }
            }

            // If no process is ready, increment the current time
            if (shortestIndex == -1) {
                currentTime++;
                continue;
            }

            int processId = processList.get(shortestIndex)[0];
            int burstTime = processList.get(shortestIndex)[1];

            // If process changes, finalize the previous block
            if (processId != previousProcessId && previousProcessId != -1) {
                addGanttBlock(table, previousProcessId, blockStartTime, currentTime, totalBurstTime);
                blockStartTime = currentTime; // Reset block start time
            }

            // Execute the entire process (non-preemptive)
            currentTime += burstTime;
            processList.get(shortestIndex)[1] = 0; // Mark process as completed
            previousProcessId = processId;
        }

        // Add the final block for the last process
        if (previousProcessId != -1) {
            addGanttBlock(table, previousProcessId, blockStartTime, currentTime, totalBurstTime);
        }

        // Final refresh to display Gantt chart
        table.revalidate();
        table.repaint();
    }
    
}