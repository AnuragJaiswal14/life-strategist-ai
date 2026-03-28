import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class LogsService {
  constructor(private prisma: PrismaService) {}

  async createLog(userId: string, data: any) {
    return this.prisma.dailyLog.create({
      data: {
        userId,
        date: new Date(),
        energyLevel: data.energyLevel,
        timeSpent: data.timeSpent || {},
        habits: data.habits || {},
      },
    });
  }

  async getLogs(userId: string) {
    return this.prisma.dailyLog.findMany({
      where: { userId },
      orderBy: { date: 'desc' },
    });
  }
}
