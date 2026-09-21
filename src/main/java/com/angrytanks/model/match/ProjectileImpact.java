package com.angrytanks.model.match;

import com.angrytanks.model.entity.Actor;
import com.angrytanks.model.entity.Projectile;
import com.angrytanks.model.geometry.Point2;

public record ProjectileImpact(Projectile projectile, Actor target, Point2 position) {}
