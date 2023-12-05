import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { catchError, tap } from 'rxjs';

import { Team } from '../../../interfaces/Team';
import { TeamImp } from '../../../classes/team/team-imp';
import { TeamService } from '../../../services/team/team.service';

@Component({
  selector: 'app-team-create',
  templateUrl: './team-create.component.html',
  styleUrls: ['./team-create.component.css']
})
export class TeamCreateComponent {

  team: Team = new TeamImp();

  constructor(private router: Router, private teamService: TeamService) {
  }

  createTeam(): void {
    const formattedTeam = this.formatToRequest(this.team);
    let op: boolean = confirm('Deseja criar a turma?');
    if (op)
      this.teamService.createTeam(formattedTeam)
        .pipe(
          tap((response): void => {
            this.router.navigate(['/team-list']);
          }),
          catchError(err => {
            throw err;
          })
        )
        .subscribe();
  }

  cancel(): void {
    this.router.navigate(['/team-list']);
  }

  formatToRequest(team: Team) {
    return {
      name: team.name,
      grade: team.grade,
      classroom: team.classroom,
      teacher: {
        id: team.teacherId
      },
      studentIds: team.studentIds.map((studentId: number) => studentId)
    };
  }

}
