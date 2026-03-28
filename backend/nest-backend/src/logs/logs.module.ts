import { Module } from '@nestjs/common';
import { LogsService } from './logs.service';
import { LogsController } from './logs.controller';
import { AiModule } from '../ai/ai.module';
import { PrismaModule } from '../prisma/prisma.module';

@Module({
  imports: [AiModule, PrismaModule],
  providers: [LogsService],
  controllers: [LogsController]
})
export class LogsModule {}
