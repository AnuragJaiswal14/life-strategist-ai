import { Controller, Post, Body, Req, UseGuards, Get } from '@nestjs/common';
import { LogsService } from './logs.service';
import { AiService } from '../ai/ai.service';
import { FirebaseAuthGuard } from '../auth/firebase-auth.guard';

@Controller('logs')
@UseGuards(FirebaseAuthGuard)
export class LogsController {
  constructor(
    private readonly logsService: LogsService,
    private readonly aiService: AiService,
  ) {}

  @Post('chat')
  async chatLog(@Body() body: { messages: { role: 'user' | 'assistant'; content: string }[] }, @Req() req: any) {
    const userId = req.user?.uid || 'anonymous-chat-user';
    const aiResponse = await this.aiService.processChat(body.messages);

    if (aiResponse.includes('__LOG_COMPLETE__')) {
      try {
        const jsonStart = aiResponse.indexOf('{');
        const jsonEnd = aiResponse.lastIndexOf('}') + 1;
        if (jsonStart !== -1 && jsonEnd !== -1) {
          const parsed = JSON.parse(aiResponse.substring(jsonStart, jsonEnd));
          if (parsed.__LOG_COMPLETE__) {
            const newLog = await this.logsService.createLog(userId, parsed);
            return {
              status: 'completed',
              message: "Awesome! I've successfully saved your daily log to your profile. See you tomorrow! ✅",
              log: newLog
            };
          }
        }
      } catch (e) {
        console.error("Failed to parse AI completion strict JSON", e);
      }
    }

    return { status: 'chatting', message: aiResponse };
  }

  @Post()
  async createManualLog(@Body() body: any, @Req() req: any) {
    return this.logsService.createLog(req.user?.uid || 'anonymous-user', body);
  }

  @Get()
  async getLogs(@Req() req: any) {
    return this.logsService.getLogs(req.user?.uid || 'anonymous-user');
  }
}
