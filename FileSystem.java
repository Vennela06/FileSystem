package OS;

import java.util.ArrayList;
import java.util.Scanner;

public class FileSystem {
	static ArrayList<Integer> dn = new ArrayList<Integer>();

	public static void main(String[] args) {
		try {
			int directoryNumber = 0;
			int blockCount = 0;
			int fileBlockCount = 0;
			int blocks = 100;
			int currentFreeLocation = -1;
			int lastOpenedFileBlkLocation = -1;
			ArrayList<DirBlock> directoryBlocks = new ArrayList<DirBlock>();
			ArrayList<FileBlock> fileBlocks = new ArrayList<FileBlock>();
			DirBlock root = new DirBlock();
			blockCount++;
			blocks--;
			directoryBlocks.add(root);
			root.free = 1;
			root.filler = 0;
			Scanner sc = new Scanner(System.in);
			String lastInput = "";
			String lastOpenedFile = null;
			String lastCreatedFile = null;
			int lastCreatedFileBlkLocation = -1;
			int position = 0;
			System.out.println(
					"Enter the following commands to create file system:\n1.Create\n2.Open\n3.Close\n4.Delete\n5.Read\n6.Write\n7.Seek\n8.End");
			String input = sc.nextLine();
			while (!input.equals("End") && blocks != 0) {

				if (input.contains("Create")) {
					if (input.startsWith("Create D")) {
						int temp = 0;
						boolean exists = false;
						String name = input.split(" ")[2];
						// Check whether directory already exists
						do {
							for (int i = 0; i < 31; i++) {
								if (String.valueOf(directoryBlocks.get(directoryNumber).content[i].name).equals(name)) {
									exists = true;
									break;
								}
							}
							directoryNumber = directoryBlocks.get(directoryNumber).frwd;
						} while (directoryNumber > -1 && exists == false);
						// Check for free location in root directory
						directoryNumber = 0;
						if (exists == false) {
							do {

								for (int i = 0; i < 31; i++) {
									if (directoryBlocks.get(directoryNumber).content[i].type == 'F') {
										currentFreeLocation = i;
										break;
									}

								}
								if (currentFreeLocation == -1) {
									temp = directoryNumber;
									directoryNumber = directoryBlocks.get(directoryNumber).frwd;
								}
							} while (directoryNumber > -1 && currentFreeLocation == -1);
							// Create new extension directory under root
							if (currentFreeLocation == -1 && exists == false) {
								directoryBlocks.add(new DirBlock());
								blocks--;
								blockCount++;
								root.free = root.free + 1;
								directoryBlocks.get(blockCount - 1).filler = blockCount - 1;
								if (directoryNumber == -1)
									directoryBlocks.get(blockCount - 1).back = directoryBlocks.get(temp).filler;
								else
									directoryBlocks.get(blockCount - 1).back = directoryBlocks
											.get(directoryNumber).filler;
								currentFreeLocation = 0;
								directoryNumber = blockCount - 1;
							}
							if (blocks == 0) {
								System.out.println("Number of blocks exceeded. Cannot create the requested one");
								break;
							}
							directoryBlocks.add(new DirBlock());
							blocks--;
							blockCount++;
							root.free = root.free + 1;
							directoryBlocks.get(blockCount - 1).filler = blockCount - 1;
							directoryBlocks.get(blockCount - 1).back = directoryBlocks.get(directoryNumber).filler;
							directoryBlocks.get(directoryNumber).content[currentFreeLocation].name = name.toCharArray();
							directoryBlocks.get(directoryNumber).content[currentFreeLocation].type = 'D';
							directoryBlocks.get(directoryNumber).content[currentFreeLocation].link = blockCount - 1;
							System.out.println("Directory '"
									+ String.valueOf(
											directoryBlocks.get(directoryNumber).content[currentFreeLocation].name)
									+ "' has been created");
							directoryNumber = 0;
							currentFreeLocation = -1;
						} else {
							System.out.println("Specified directory already exists");
						}
					} else if (input.startsWith("Create U")) {
						String name = input.split(" ")[2];
						String[] filePath = name.split("/");
						int directoryLevel = 0;
						int temp = 0;
						boolean fileExists = false;
						boolean dirExists = false;
						int freeLocation = -1;
						if (filePath.length >= 1) {
							for (int i = 0; i < filePath.length; i++) {
								if (i == filePath.length - 1) {
									temp = directoryLevel;
									do {
										for (int j = 0; j < 31; j++) {
											if (String.valueOf(directoryBlocks.get(directoryLevel).content[j].name)
													.equals(filePath[i])
													&& directoryBlocks.get(directoryLevel).content[j].type == 'U') {
												fileExists = true;
												directoryBlocks.get(directoryLevel).content[j].size = 0;
												fileBlocks.get(directoryBlocks
														.get(directoryLevel).content[j].link).content = ""
																.toCharArray();
												System.out.println("File '" + filePath[i] + "' has been overwritten");
												break;
											}

										}
										directoryLevel = directoryBlocks.get(directoryLevel).frwd;
									} while (directoryLevel > -1 && fileExists == false);
									// create file block
									if (fileExists == false) {
										if (directoryLevel == -1)
											directoryLevel = temp;
										do {
											for (int j = 0; j < 31; j++) {
												if (directoryBlocks.get(directoryLevel).content[j].type == 'F') {
													freeLocation = j;
													break;
												}

											}
											if (freeLocation == -1) {
												temp = directoryLevel;
												directoryLevel = directoryBlocks.get(directoryLevel).frwd;
											}
										} while (directoryLevel > -1 && freeLocation == -1);
										if (freeLocation == -1) {
											if (blocks != 0) {
												directoryBlocks.add(new DirBlock());
												blocks--;
												blockCount++;
												root.free = root.free + 1;
												if (directoryLevel < 0)
													directoryBlocks.get(blockCount - 1).back = directoryBlocks
															.get(temp).filler;
												else
													directoryBlocks.get(blockCount - 1).back = directoryBlocks
															.get(directoryLevel).filler;
												directoryBlocks.get(blockCount - 1).filler = blockCount - 1;
												freeLocation = 0;
												directoryLevel = blockCount - 1;
											} else {
												System.out.println(
														"Number of blocks exceeded. Cannot create the requested one");
											}
										}
										if (blocks != 0) {
											fileBlocks.add(new FileBlock());
											root.free = root.free + 1;
											blocks--;
											fileBlockCount++;
											directoryBlocks.get(directoryLevel).content[freeLocation].name = filePath[i]
													.toCharArray();
											directoryBlocks.get(directoryLevel).content[freeLocation].type = 'U';
											directoryBlocks.get(directoryLevel).content[freeLocation].size = 0;
											directoryBlocks
													.get(directoryLevel).content[freeLocation].link = fileBlockCount
															- 1;
											fileBlocks.get(fileBlockCount - 1).back = directoryBlocks
													.get(directoryLevel).filler;
											System.out.println("File '" + String.valueOf(
													directoryBlocks.get(directoryLevel).content[freeLocation].name)
													+ "' has been created");
											lastCreatedFile = name;
											lastCreatedFileBlkLocation = fileBlockCount - 1;
										} else {
											System.out.println(
													"Number of blocks exceeded. Cannot create the requested one");
										}
									}
								} else {
									dirExists = false;
									temp = directoryLevel;
									do {
										for (int j = 0; j < 31; j++) {
											if (filePath[i].equals(
													String.valueOf(directoryBlocks.get(directoryLevel).content[j].name))
													&& directoryBlocks.get(directoryLevel).content[j].type == 'D') {
												directoryLevel = directoryBlocks.get(directoryLevel).content[j].link;
												dirExists = true;
												break;
											}
										}
										if (!dirExists) {
											directoryLevel = directoryBlocks.get(directoryLevel).frwd;
										}
									} while (directoryLevel > -1 && !dirExists);
									if (!dirExists) {
										// Create a directory at current level with given sub-directory name
										directoryLevel = temp;
										for (int j = 0; j < 31; j++) {
											if (directoryBlocks.get(directoryLevel).content[j].type == 'F') {
												freeLocation = j;
												break;
											}
										}
										if (freeLocation == -1) {
											while (directoryBlocks.get(directoryLevel).frwd > -1) {
												directoryLevel = directoryBlocks.get(directoryLevel).frwd;
												for (int z = 0; z < 31; z++) {
													if (directoryBlocks.get(directoryLevel).content[z].type == 'F') {
														freeLocation = z;
														break;
													}
												}
												if (freeLocation > -1) {
													break;
												}
											}
											if (freeLocation < 0 && directoryBlocks.get(directoryLevel).frwd < 0) {
												if (blocks != 0) {
													directoryBlocks.add(new DirBlock());
													root.free = root.free + 1;
													blockCount++;
													blocks--;
													directoryBlocks.get(blockCount - 1).filler = blockCount - 1;
													directoryBlocks.get(blockCount - 1).back = directoryBlocks
															.get(directoryLevel).filler;
													freeLocation = 0;
													directoryLevel = blockCount - 1;
												} else {
													System.out.println(
															"Number of blocks exceeded. Cannot create the requested one");
												}

											}
										}
										if (blocks != 0) {
											directoryBlocks.add(new DirBlock());
											blockCount++;
											root.free = root.free + 1;
											blocks--;
											directoryBlocks.get(directoryLevel).content[freeLocation].type = 'D';
											directoryBlocks.get(directoryLevel).content[freeLocation].name = filePath[i]
													.toCharArray();
											directoryBlocks.get(directoryLevel).content[freeLocation].link = blockCount
													- 1;
											directoryBlocks.get(blockCount - 1).filler = blockCount - 1;
											directoryBlocks.get(blockCount - 1).back = directoryBlocks
													.get(directoryLevel).filler;
											directoryLevel = blockCount - 1;
										} else {
											System.out.println(
													"Number of blocks exceeded. Cannot create the requested one");
										}
									}
								}

							}
						}
						directoryLevel = 0;
						freeLocation = -1;
						temp = 0;
					}
				} else if (input.contains("Open")) {
					String fileName = input.split(" ")[2];
					lastOpenedFile = fileName;
					if (input.startsWith("Open I")) {
						int fileBlkLocation = findFile(fileName, directoryBlocks);
						if (fileBlkLocation > -1) {
							lastOpenedFileBlkLocation = fileBlkLocation;
							System.out.println("File " + fileName + " opened in Input mode");
						}
						else {
							System.out.println("File "+fileName+" not found");
						}
					} else if (input.startsWith("Open O")) {
						int fileBlkLocation = findFile(fileName, directoryBlocks);
						if (fileBlkLocation > -1) {
							lastOpenedFileBlkLocation = fileBlkLocation;
							System.out.println("File " + fileName + " opened in Output mode");
						}
						else {
							System.out.println("File "+fileName+" not found");
						}
					} else if (input.startsWith("Open U")) {
						int fileBlkLocation = findFile(fileName, directoryBlocks);
						if (fileBlkLocation > -1) {
							lastOpenedFileBlkLocation = fileBlkLocation;
							System.out.println("File " + fileName + " opened in Update mode");
						}
						else {
							System.out.println("File "+fileName+" not found");
						}
					} else {
						System.out.println("Given command is incorrect");
					}

				} else if (input.contains("Close")) {

					if (lastOpenedFile == null) {
						if (lastCreatedFile == null)
							System.out.println("There is no recently created or opened file");
						else {
							System.out.println("Closed the recently created file " + lastCreatedFile);
							lastCreatedFile = null;
						}
					} else {
						System.out.println("Closed the recently opened file " + lastOpenedFile);
						lastOpenedFile = null;
					}

				} else if (input.contains("Delete")) {
					String fileName = input.split(" ")[1];
					int fileBlkLocation = findFile(fileName, directoryBlocks);
					if (fileBlkLocation == -1) {
						System.out.println("The specified file " + fileName + " doesn't exists");
					} else {
						int back = fileBlocks.get(fileBlkLocation).back;
						for (int i = 0; i < directoryBlocks.size(); i++) {
							if (directoryBlocks.get(i).filler == back) {
								for (int j = 0; j < 31; j++) {
									if (String.valueOf(directoryBlocks.get(i).content[j].name).equals(fileName)
											&& directoryBlocks.get(i).content[j].type == 'U') {
										directoryBlocks.get(i).content[j].name = "".toCharArray();
										directoryBlocks.get(i).content[j].type = 'F';
										directoryBlocks.get(i).content[j].size = 0;
										directoryBlocks.get(i).content[j].link = -1;
										System.out.println("File " + fileName + " deleted");
										break;
									}
								}
							}

						}
						fileBlocks.get(fileBlkLocation).back = -1;
						fileBlocks.get(fileBlkLocation).content = null;
						int temp = fileBlocks.get(fileBlkLocation).frwd;
						fileBlocks.get(fileBlkLocation).frwd = -1;
						blocks++;
						int frwdBlock = temp;
						while (frwdBlock >= 0) {
							fileBlocks.get(frwdBlock).back = -1;
							fileBlocks.get(frwdBlock).content = null;
							temp = fileBlocks.get(frwdBlock).frwd;
							fileBlocks.get(frwdBlock).frwd = -1;
							frwdBlock = temp;
							blocks++;
						}
					}
				} else if (input.contains("Read")) {
					if (lastInput.startsWith("Open I") || lastInput.startsWith("Open U")) {
						int n = Integer.parseInt(input.split(" ")[1]);
						int temp = lastOpenedFileBlkLocation;
						if (lastInput.startsWith("Open I"))
							position = 0;
						while (n > 0 && temp > -1) {
							if (n <= 252) {
								for (int i = position; i < n + position; i++) {
									if(fileBlocks.get(temp).content[i]==' ')
										System.out.print(fileBlocks.get(temp).content[i]);
									else if ((int) fileBlocks.get(temp).content[i] == 0) {
										System.out.print("\nEnd of file reached");
										break;
									} else
										System.out.print(fileBlocks.get(temp).content[i]);
								}
							} else {
								for (int i = position; i < 252; i++) {
									if ((int) fileBlocks.get(temp).content[i] == 0) {
										System.out.print("\nEnd of file reached");
										break;
									} else
										System.out.print(fileBlocks.get(temp).content[i]);
								}

								position = 0;
								temp = fileBlocks.get(temp).frwd;
							}
							n = n - 252;

							System.out.print("\n");
						}

					} else {
						System.out.println("Previous input should be 'Open' in I or U mode for Read command");
					}
				} else if (input.contains("Write")) {
					if (lastInput.startsWith("Open U") || lastInput.startsWith("Open O")
							|| lastInput.startsWith("Create")) {
						int n = Integer.parseInt(input.split(" ")[1]);
						int temp = -1;
						if (lastInput.startsWith("Open"))
							temp = lastOpenedFileBlkLocation;
						if (lastInput.startsWith("Create"))
							temp = lastCreatedFileBlkLocation;
						String data = input.split(n+" ")[1].split("'")[1];
						char[] dataChar = data.toCharArray();
						int i = 0;
						while ((fileBlocks.get(temp).content[i]==' '||(int) fileBlocks.get(temp).content[i] != 0) && i != 252) {
							i++;
							if (i == 252) {
								if (fileBlocks.get(temp).frwd > -1)
									temp = fileBlocks.get(temp).frwd;
								else {
									if (blocks != 0) {
										fileBlocks.add(new FileBlock());
										blocks--;
										fileBlockCount++;
										fileBlocks.get(fileBlockCount - 1).back = temp;
										fileBlocks.get(temp).frwd = fileBlockCount - 1;
										temp = fileBlockCount - 1;
									} else {
										System.out.println(
												"Number of blocks exceeded. Cannot create new one to perform specifed operation");
									}
								}
								i = 0;
							}

						}
						for (int j = 0; j < dataChar.length; j++) {
							if (i != 252) {
								if (n == 0)
									break;
								fileBlocks.get(temp).content[i] = dataChar[j];
								i++;
								n--;
							}
						}
						while (n != 0) {
							fileBlocks.get(temp).content[i] = ' ';
							n--;
							i++;
						}
						System.out.println("Written to file");
						int temp1 = -1;
						int fileLink = -1;
						if (lastInput.startsWith("Create")) {
							temp1 = fileBlocks.get(lastCreatedFileBlkLocation).back;
							fileLink = lastCreatedFileBlkLocation;
						}
						if (lastInput.startsWith("Open")) {
							temp1 = fileBlocks.get(lastOpenedFileBlkLocation).back;
							fileLink = lastOpenedFileBlkLocation;
						}
						position = i - 1;
						for (int z = 0; z < directoryBlocks.size(); z++) {
							if (directoryBlocks.get(z).filler == temp1) {
								for (int j = 0; j < 31; j++) {
									if (directoryBlocks.get(z).content[j].link == fileLink
											&& directoryBlocks.get(z).content[j].type == 'U') {
										directoryBlocks.get(z).content[j].size = (short) (i - 1);
										break;
									}
								}
							}

						}
					}
				} else if (input.contains("Seek")) {
					int base = Integer.parseInt(input.split(" ")[1]);
					int offset = Integer.parseInt(input.split(" ")[2]);
					if (lastInput.startsWith("Open")) {
						if (base == 0) {
							if (offset == -1) {
								position = 0;
							System.out.println("Current position:" + position);
							}
							else {
								position = position + offset;
								System.out.println("Current position:" + position);
							}

						} else if (base == -1) {
							position = 0 + offset;
							System.out.println("Current position:" + position);

						} else if (base == 1) {
							int i = 0;
							while ((int) fileBlocks.get(lastOpenedFileBlkLocation).content[i] != 0) {
								i++;
							}

							position = i - 1 + offset;
							System.out.println("Current position:" + position);
						}
					} else {
						System.out.println(
								"Seek operation will work along with Open command. So, previous command should be Open");
					}
				}
				lastInput = input;
				input = sc.nextLine();
				if (blocks == 0 && input.startsWith("Create")) {
					System.out.println("Number of blocks exceeded. Cannot create the requested one");
				}
			}
			displayFileSystemStructure(directoryBlocks);
		} catch (

		Exception e) {
			throw e;
		}

	}

	private static void displayDir(ArrayList<DirBlock> directoryBlocks, int filler) {
		ArrayList<Integer> d = new ArrayList<Integer>();
		String size = null;
		String dirName = "";
		int dir = 0;
		if (filler == 0)
			System.out.println("Root directory structure:");
		else {
			for (int i = 0; i < directoryBlocks.size(); i++) {
				for (int j = 0; j < 31; j++) {
					if (directoryBlocks.get(i).content[j].link == filler) {
						dirName = String.valueOf(directoryBlocks.get(i).content[j].name);
						break;
					}
				}
				if (!dirName.equals(""))
					break;
			}
			System.out.println("Directory '" + dirName + "' structure:");
		}
		System.out.println("Type\t Name\t Size\t");
		for (int i = 0; i < directoryBlocks.size(); i++) {
			if (directoryBlocks.get(i).filler == filler)
				dir = i;
		}
		do {
			for (int j = 0; j < 31; j++) {
				if (directoryBlocks.get(dir).content[j].type == 'D') {
					d.add(directoryBlocks.get(dir).content[j].link);
					size = "Not Applicable";
				} else {
					size = String.valueOf(directoryBlocks.get(dir).content[j].size);
				}
				System.out.println(directoryBlocks.get(dir).content[j].type + "\t"
						+ String.valueOf(directoryBlocks.get(dir).content[j].name) + "\t" + size);
			}
			dir = directoryBlocks.get(dir).frwd;
		} while (dir > -1);
		d.forEach((n) -> displayDir(directoryBlocks, n));
	}

	private static void displayFileSystemStructure(ArrayList<DirBlock> directoryBlocks) {
		// Traverse root directory
		dn.add(0);
		System.out.println("The file system struture is as follows:");
		dn.forEach((n) -> displayDir(directoryBlocks, n));

	}

	private static int traverseDirectory(String fileName, int directoryNumber, ArrayList<DirBlock> directoryBlocks) {
		for (int i = 0; i < 31; i++) {
			if (directoryBlocks.get(directoryNumber).content[i].type == 'U'
					&& String.valueOf(directoryBlocks.get(directoryNumber).content[i].name).equals(fileName)) {
				return directoryBlocks.get(directoryNumber).content[i].link;
			}
		}
		for (int i = 0; i < 31; i++) {
			if (directoryBlocks.get(directoryNumber).content[i].type == 'D') {
				return traverseDirectory(fileName, directoryBlocks.get(directoryNumber).content[i].link,
						directoryBlocks);
			}
		}
		return -1;

	}

	private static int findFile(String fileName, ArrayList<DirBlock> directoryBlocks) {
		int directoryNumber = 0;
		int temp = traverseDirectory(fileName, directoryNumber, directoryBlocks);
		while (temp != -1 && directoryBlocks.get(directoryNumber).frwd != -1) {
			temp = traverseDirectory(fileName, directoryBlocks.get(directoryNumber).frwd, directoryBlocks);
		}
		return temp;
	}
}

class DirBlock {
	int frwd;
	int back;
	int free;
	int filler;
	DirContent[] content = new DirContent[31];

	DirBlock() {
		this.frwd = -1;
		this.back = -1;
		this.free = -1;
		this.filler = -1;
		for (int i = 0; i < 31; i++) {
			this.content[i] = new DirContent();
		}
	}

}

class FileBlock {
	int frwd = -1;
	int back = -1;
	char[] content = new char[252];

	FileBlock() {
		this.frwd = -1;
		this.back = -1;
	}
}

class DirContent {
	char type;
	char[] name = new char[4];
	int link;
	short size;

	DirContent() {
		this.type = 'F';
		this.link = -1;
		this.size = 0;

	}
}
